package com.petra.lib.operation.actor;

import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.operation.OperationService;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextManager;
import lombok.Getter;

import java.util.UUID;


public class LocalProducer {

    @Getter
    private final Identifier workflowId;
    private final RemoteConsumer firstConsumer;
    private final ValueContextManager valueContextManager;
    private final ContextService contextService;


    LocalProducer(Identifier workflowId,
                  RemoteConsumer firstConsumer,
                  ValueContextManager valueContextManager, ContextService contextService, OperationService operationService) {
        this.workflowId = workflowId;
        this.firstConsumer = firstConsumer;
        this.valueContextManager = valueContextManager;
        this.contextService = contextService;

    }

    public void start(Context context, OperationService operationService) {
        WorkflowContext workflowContext = contextService.createWorkflowContext(context.getScenarioId(), workflowId);
        workflowContext.create();

        firstConsumer.execute(context.getContextInputValues(), context.getScenarioId(), this::skip,
                operationService, this::error);
    }


    public void answerFromBlock(ValueContainer answerContainer, Identifier answerBlockId, UUID scenarioId,
                                ExecutionStatus execResult, OperationService operationService) {

        RemoteConsumer workConsumer = firstConsumer;
        ConsumerIdentifier answerConsumerId = new ConsumerIdentifier(answerBlockId, workflowId);
        Context workflowContext = contextService.createContext(workflowId, scenarioId);
        if (isWorkflowDone((workflowContext))){
            return;
        }
        
        do {
            if (workConsumer.getId().equals(answerConsumerId)) {
                WorkflowContext valuesContext = updateContextValues(answerContainer, scenarioId);

                if (ExecutionStatus.ERROR == execResult) {
                    end(null, workflowContext, false, operationService);
                    return;
                }

                if (workConsumer.hasNext()) {
                    RemoteConsumer nextConsumer = workConsumer.next();
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

    private boolean isWorkflowDone(Context workflowContext){
        workflowContext.load();
        return workflowContext.getCurrentState() == ContextState.ANSWERED;
    }


    private WorkflowContext updateContextValues(ValueContainer answerContainer, UUID scenarioId) {
        WorkflowContext workflowContext = contextService.createWorkflowContext(scenarioId, workflowId);
        boolean loaded = workflowContext.lockAndLoad();
        if (!loaded) {
            throw new IllegalStateException("Context not found");
        }

        ValueContainer contextContainer = workflowContext.getContextValues();
        answerContainer.getValues().forEach(contextContainer::setValue);
        workflowContext.setContextValues(contextContainer);
        workflowContext.unlockAndSave();
        return workflowContext;
    }

    private void skip(UUID scenarioId, RemoteConsumer remoteConsumer, OperationService service) {
        WorkflowContext workflowContext = contextService.createWorkflowContext(scenarioId, workflowId);
        workflowContext.load();
        if (remoteConsumer.hasNext()) {
            remoteConsumer.next().execute(workflowContext.getContextValues(), scenarioId,
                    this::skip, service, this::error);
        } else {
            Context context = contextService.createContext(workflowId, scenarioId);
            end(workflowContext.getContextValues(), context, true, service);
        }
    }

    private void error(UUID scenarioId, RemoteConsumer remoteConsumer, OperationService service) {
        WorkflowContext workflowContext = contextService.createWorkflowContext(scenarioId, workflowId);
        workflowContext.load();
        Context context = contextService.createContext(workflowId, scenarioId);
        end(null, context, false, service);
    }


    private void end(ValueContainer answerContainer, Context context, boolean isSuccess, OperationService operationService) {

        if (!isSuccess) {
            context.lockAndLoad();
            boolean stateResult = context.setState(ContextState.EXECUTED);
            if (stateResult) {
                context.setExecutionStatus(ExecutionStatus.ERROR);
            }
            context.unlockAndSave();
            operationService.executeState(context);
            return;
        }

        //вызывать парсинг из последнего блока в выходные переменные воркфлоу
        valueContextManager.start(answerContainer, context.getScenarioId(), new VariableCallback() {
            @Override
            public void loaded(ValueContainer loadedValues) {
                context.lockAndLoad();
                boolean stateResult = context.setState(ContextState.EXECUTED);
                if (stateResult) {
                    context.setOutValues(loadedValues);
                    context.setExecutionStatus(ExecutionStatus.OK);
                }
                context.unlockAndSave();
                operationService.executeState(context);

            }

            @Override
            public void error(Exception e) {
                context.lockAndLoad();
                boolean stateResult = context.setState(ContextState.EXECUTED);
                if (stateResult) {
                    context.setExecutionStatus(ExecutionStatus.ERROR);
                }
                context.unlockAndSave();
                operationService.executeState(context);
            }
        });
    }

}
