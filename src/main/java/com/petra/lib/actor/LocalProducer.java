package com.petra.lib.actor;

import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.operation.OperationService;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;


@Slf4j
public class LocalProducer {

    @Getter
    private final Identifier workflowId;
    private final RemoteConsumer firstConsumer;

    @Getter
    private final String workflowName;

    /**
     * Выходные переменные воркфлоу
     */
    private final ValueContextManager valueContextExitManager;
    private final ContextService contextService;
    private final TransactionManager transactionManager;

    LocalProducer(Identifier workflowId,
                  RemoteConsumer firstConsumer, String workflowName,
                  ValueContextManager valueContextExitManager, ContextService contextService,
                  TransactionManager transactionManager) {
        this.workflowId = workflowId;
        this.firstConsumer = firstConsumer;
        this.valueContextExitManager = valueContextExitManager;
        this.contextService = contextService;

        this.workflowName = workflowName;
        this.transactionManager = transactionManager;
    }

    public void start(Context context, OperationService operationService) {
        WorkflowContext workflowContext;
        try (Transaction transaction = transactionManager.createNewTransaction(false, null)) {
            workflowContext = contextService.createWorkflowContext(context.getScenarioId(), workflowId);
            workflowContext.create(context.getContextInputValues(), transaction);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("{} Start executing workflow {}", context.getScenarioId(), workflowName);
        firstConsumer.execute(context.getContextInputValues(), context.getScenarioId(), this::skip,
                operationService, this::error);
    }


    public void answerFromBlock(ValueContainer answerContainer, Identifier answerBlockId, UUID scenarioId,
                                ExecutionStatus execResult, OperationService operationService) {


        RemoteConsumer workConsumer = firstConsumer;
        ConsumerIdentifier answerConsumerId = new ConsumerIdentifier(answerBlockId, workflowId);
        Context workflowContext = contextService.createContext(workflowId, scenarioId);
        if (isWorkflowDone((workflowContext))) {
            log.info("{} repeat detection for workflow: {}, skipping...", scenarioId, workflowName);
            return;
        }

        do {
            if (workConsumer.getId().equals(answerConsumerId)) {
                updateContextValues(answerContainer, scenarioId);
                log.info("{} workflow {} get answer {} from block {}", scenarioId, workflowName, execResult,
                        workConsumer.getConsumerName());
                if (ExecutionStatus.ERROR == execResult) {
                    end(null, workflowContext, false, operationService);
                    return;
                }

                if (workConsumer.hasNext()) {
                    RemoteConsumer nextConsumer = workConsumer.next();
                    WorkflowContext valuesContext = contextService.createWorkflowContext(scenarioId, workflowId);
                    try (Transaction transaction = transactionManager.createNewTransaction(true, null)) {
                        valuesContext.load(transaction);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    nextConsumer.execute(valuesContext.getContextValues(), scenarioId, this::skip,
                            operationService, this::error);
                    return;
                }
            }

            if (!workConsumer.hasNext()) {
                break;
            }
            workConsumer = workConsumer.next();
        } while (true);


        end(answerContainer, workflowContext, true, operationService);
    }

    private boolean isWorkflowDone(Context workflowContext) {
        try (Transaction transaction = transactionManager.createNewTransaction(true, null)) {
            workflowContext.load(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return workflowContext.getCurrentState() == ContextState.ANSWERED;
    }


    private void updateContextValues(ValueContainer answerContainer, UUID scenarioId) {

        try (Transaction tx = transactionManager.createNewTransaction(false, null)) {
            WorkflowContext workflowContext = contextService.createWorkflowContext(scenarioId, workflowId);

            boolean loaded = workflowContext.lockAndLoad(tx);
            if (!loaded) {
                tx.rollback();
                throw new IllegalStateException("Context not found");
            }

            ValueContainer contextContainer = workflowContext.getContextValues();
            answerContainer.getValues().forEach(contextContainer::setValue);
            workflowContext.setContextValues(contextContainer);
            workflowContext.save(tx);
            tx.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void skip(UUID scenarioId, RemoteConsumer remoteConsumer, OperationService service) {
        WorkflowContext workflowContext = contextService.createWorkflowContext(scenarioId, workflowId);
        try (Transaction transaction = transactionManager.createNewTransaction(true, null)) {
            workflowContext.load(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (remoteConsumer.hasNext()) {
            remoteConsumer.next().execute(workflowContext.getContextValues(), scenarioId,
                    this::skip, service, this::error);
        } else {
            Context context = contextService.createContext(workflowId, scenarioId);

            try (Transaction transaction = transactionManager.createNewTransaction(true, null)) {
                context.load(transaction);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            end(workflowContext.getContextValues(), context, true, service);
        }
    }

    private void error(UUID scenarioId, RemoteConsumer remoteConsumer, OperationService service) {
        WorkflowContext workflowContext = contextService.createWorkflowContext(scenarioId, workflowId);
        try (Transaction transaction = transactionManager.createNewTransaction(true, null)) {
            workflowContext.load(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Context context = contextService.createContext(workflowId, scenarioId);
        try (Transaction tr = transactionManager.createNewTransaction(true, null)) {
            context.load(tr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        end(null, context, false, service);
    }


    private void end(ValueContainer answerContainer, Context context, boolean isSuccess, OperationService operationService) {

        log.info("{} workflow {} finished with success={}", context.getScenarioId(), workflowName, isSuccess);
        if (!isSuccess) {
            try (Transaction tr = transactionManager.createNewTransaction(false, null)) {
                context.lockAndLoad(tr);
                boolean stateResult = context.setState(ContextState.EXECUTED);
                if (stateResult) {
                    context.setExecutionStatus(ExecutionStatus.ERROR);
                }else {
                    tr.rollback();
                }
                context.save(tr);

                WorkflowContext wfCtx = contextService.createWorkflowContext(context.getScenarioId(), workflowId);
                wfCtx.lockAndLoad(tr);
                wfCtx.setState(WorkflowContextState.ERROR);
                wfCtx.save(tr);

                operationService.executeState(context);
                return;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //вызывать парсинг из последнего блока в выходные переменные воркфлоу
        valueContextExitManager.start(answerContainer, context.getScenarioId(), new VariableCallback() {
            @Override
            public void loaded(ValueContainer loadedValues) {
                try (Transaction tr = transactionManager.createNewTransaction(false, null)) {
                    context.lockAndLoad(tr);
                    boolean stateResult = context.setState(ContextState.EXECUTED);
                    if (stateResult) {
                        context.setOutValues(loadedValues);
                        context.setExecutionStatus(ExecutionStatus.OK);
                    }else {
                        tr.rollback();
                        return;
                    }
                    context.save(tr);

                    WorkflowContext wfCtx = contextService.createWorkflowContext(context.getScenarioId(), workflowId);
                    wfCtx.lockAndLoad(tr);
                    wfCtx.setState(WorkflowContextState.DONE);
                    wfCtx.save(tr);

                    operationService.executeState(context);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void error(Exception e) {
                log.error("{} exit variables {} error {}",context.getScenarioId(), workflowName,e);
                try (Transaction tr = transactionManager.createNewTransaction(false, null)) {
                    context.lockAndLoad(tr);
                    boolean stateResult = context.setState(ContextState.EXECUTED);
                    if (stateResult) {
                        context.setExecutionStatus(ExecutionStatus.ERROR);
                    }else {
                        tr.rollback();
                        return;
                    }
                    context.save(tr);

                    WorkflowContext wfCtx = contextService.createWorkflowContext(context.getScenarioId(), workflowId);
                    wfCtx.lockAndLoad(tr);
                    wfCtx.setState(WorkflowContextState.ERROR);
                    wfCtx.save(tr);

                    operationService.executeState(context);
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }
            }
        });
    }

}
