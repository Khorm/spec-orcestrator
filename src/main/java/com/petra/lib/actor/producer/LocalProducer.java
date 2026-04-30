package com.petra.lib.actor.producer;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LocalProducer {

    @Getter
    Identifier workflowId;
    RemoteConsumerLinkedList remoteConsumerList;

    @Getter
    private final String workflowName;

    /**
     * Выходные переменные воркфлоу
     */
    private final ValueContextManager valueContextExitManager;
    private final ContextService contextService;
    private final TransactionManager transactionManager;



    public void start(Context context, OperationService operationService) {
        transactionManager.executeInTransaction(transaction -> {
            WorkflowContext wc = contextService.fillDefaultWorkflowContext(context.getScenarioId(), workflowId);
            wc.create(context.getContextInputValues(), transaction);
        });

        log.info("{} Start executing workflow {}", context.getScenarioId(), workflowName);
        remoteConsumerList.getFirstConsumer().execute(context.getContextInputValues(), context.getScenarioId(), this::answerFromBlock,
                operationService);
    }


    public void answerFromBlock(ValueContainer answerContainer, Identifier answerBlockId, UUID scenarioId,
                                ExecutionStatus execResult, OperationService operationService) {
        if (isWorkflowDone(scenarioId)) {
            log.info("{} repeat detection for workflow: {}, skipping...", scenarioId, workflowName);
            return;
        }

        ConsumerIdentifier answerConsumerId = new ConsumerIdentifier(answerBlockId, workflowId);
        Optional<RemoteConsumer> nextConsumerInWorkflowOpt = remoteConsumerList.findConsumer(answerConsumerId);
        if (nextConsumerInWorkflowOpt.isPresent()) {
            ValueContainer newWorkflowValues = updateWorkflowContextValues(answerContainer, scenarioId);
            RemoteConsumer nextConsumerInWorkflow = nextConsumerInWorkflowOpt.get();
            log.info("{} workflow {} get answer {} from block {}", scenarioId, workflowName, execResult,
                    nextConsumerInWorkflow.getConsumerName());

            if (ExecutionStatus.ERROR == execResult) {
                end(null, scenarioId, false, operationService);
                return;
            }

            //если есть следующий консумер то вызываем его
            if (nextConsumerInWorkflow.hasNext()){
                RemoteConsumer nextConsumer = nextConsumerInWorkflow.next();
                nextConsumer.execute(newWorkflowValues, scenarioId, this::answerFromBlock, operationService);
                return;
            }
        }else {
            throw new IllegalArgumentException(String.format("%s Not found next consumer", scenarioId));
        }
        end(answerContainer, scenarioId, true, operationService);
    }

    /**
     * Проверяет если выполнение блока воркфлоу еще не завершено, а статус ANSWERED
     * значит контекст обрабатывает другой под сервиса
     *
     * @return
     */
    private boolean isWorkflowDone(UUID scenarioId) {
        Context workflowContext = contextService.fillDefaultContext(workflowId, scenarioId);
        try (Transaction transaction = transactionManager.createNewTransaction(true, null)) {
            workflowContext.load(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return workflowContext.getCurrentState() == ContextState.ANSWERED;
    }


    /**
     * обновляет контекст воркфлоу новыми значениями из выполненного блока
     *
     * @param answerContainer
     * @param scenarioId
     * @return
     */
    private ValueContainer updateWorkflowContextValues(ValueContainer answerContainer, UUID scenarioId) {

        try (Transaction tx = transactionManager.createNewTransaction(false, null)) {
            WorkflowContext workflowContext = contextService.fillDefaultWorkflowContext(scenarioId, workflowId);

            boolean loaded = workflowContext.lockAndLoad(tx);
            if (!loaded) {
                tx.rollback();
                throw new IllegalStateException("Context not found");
            }

            ValueContainer contextContainer = workflowContext.getContextValues();
            answerContainer.getValues().forEach(contextContainer::setValue);
            workflowContext.setContextValues(contextContainer);
            workflowContext.save(tx);
            return workflowContext.getContextValues();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private void end(ValueContainer answerContainer, UUID scenarioId, boolean isSuccess, OperationService operationService) {
        Context context = contextService.fillDefaultContext(workflowId, scenarioId);
        log.info("{} workflow {} finished with success={}", context.getScenarioId(), workflowName, isSuccess);
        if (!isSuccess) {
            transactionManager.executeInTransaction(tr -> {
                context.lockAndLoad(tr);
                boolean stateResult = context.setState(ContextState.EXECUTED);
                if (stateResult) {
                    context.setExecutionStatus(ExecutionStatus.ERROR);
                } else {
                    tr.rollback();
                    return;
                }
                context.save(tr);

                WorkflowContext wfCtx = contextService.fillDefaultWorkflowContext(context.getScenarioId(), workflowId);
                wfCtx.lockAndLoad(tr);
                wfCtx.setState(WorkflowContextState.ERROR);
                wfCtx.save(tr);

                operationService.executeState(context);
            });
            return;
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
                    } else {
                        tr.rollback();
                        return;
                    }
                    context.save(tr);

                    WorkflowContext wfCtx = contextService.fillDefaultWorkflowContext(context.getScenarioId(), workflowId);
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
                log.error("{} exit variables {} error {}", context.getScenarioId(), workflowName, e);
                try (Transaction tr = transactionManager.createNewTransaction(false, null)) {
                    context.lockAndLoad(tr);
                    boolean stateResult = context.setState(ContextState.EXECUTED);
                    if (stateResult) {
                        context.setExecutionStatus(ExecutionStatus.ERROR);
                    } else {
                        tr.rollback();
                        return;
                    }
                    context.save(tr);

                    WorkflowContext wfCtx = contextService.fillDefaultWorkflowContext(context.getScenarioId(), workflowId);
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
