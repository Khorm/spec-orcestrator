package com.petra.lib.context.model;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.block.ContextService;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.context.block.WorkflowContextState;
import com.petra.lib.operation.OperationService;
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
    private final OperationService operationService;


    LocalProducer(Identifier workflowId,
                  RemoteConsumer firstConsumer,
                  ValueContextManager valueContextManager, ContextService contextService, OperationService operationService) {
        this.workflowId = workflowId;
        this.firstConsumer = firstConsumer;
        this.valueContextManager = valueContextManager;
        this.contextService = contextService;
        this.operationService = operationService;
    }

    public void start(Context context) {
        firstConsumer.execute(context);
    }


    public void answerFromBlock(ValueContainer answerContainer, Identifier answerBlockId, UUID scenarioId,
                                ExecutionStatus execResult) {
        if (execResult == ExecutionStatus.REPEAT){
            return;
        }

        RemoteConsumer workConsumer = firstConsumer;
        ConsumerIdentifier answerConsumerId = new ConsumerIdentifier(answerBlockId, workflowId);

        do {
            if (workConsumer.getId().equals(answerConsumerId)) {
                Context workflowContext = updateContextValues(answerContainer, scenarioId);
                workConsumer.answer(scenarioId, execResult);
                if (ExecutionStatus.ERROR == execResult) {
                    end(null, workflowContext, false);
                    return;
                }

                if (workConsumer.hasNext()) {
                    RemoteConsumer nextConsumer = workConsumer.next();
                    nextConsumer.execute(workflowContext);
                    return;
////                    WorkflowContext workflowBlockCtx = contextService.createWorkflowContext(scenarioId,workConsumer.getId());
////                    workflowBlockCtx.lockAndLoad();
//
//                    if (ExecutionStatus.OK == execResult) {
////                        answerConsumerId = workflowBlockCtx.getIdentifier();
////                        Context blockCtx = contextService.createContext(workflowBlockCtx.getIdentifier().getConsumerId(), scenarioId);
////                        blockCtx.lockAndLoad();
////                        blockCtx.unlockAndDiscard();
////                        answerContainer = blockCtx.getContextOutValues();
////                        continue;
//                    }
//
//                    if (workflowBlockCtx.getState() == WorkflowContextState.ERROR) {
////                        end(null,workflowContext, false);
////                        return;
//                    }
//
//                    workflowContext.unlockAndDiscard();
//                    workConsumer.next().execute(workflowContext);
//                    return;
                }
            }

            if (!workConsumer.hasNext()) {
                break;
            }
            workConsumer = workConsumer.next();
        } while (true);

        Context worflowContext = contextService.createContext(workflowId, scenarioId);
        end(answerContainer, worflowContext, true);
    }




    private Context updateContextValues(ValueContainer answerContainer, UUID scenarioId) {
        Context workflowContext = contextService.createContext(workflowId, scenarioId);
        boolean loaded = workflowContext.lockAndLoad();
        if (!loaded) {
            throw new IllegalStateException("Context not found");
        }
        workflowContext.setOutValues(answerContainer);
        workflowContext.unlockAndSave();
        return workflowContext;
    }

    private void end(ValueContainer answerContainer, Context context, boolean isSuccess) {

        if (!isSuccess) {
            context.lockAndLoad();
            context.saveError(null);
            operationService.executeState(context);
            return;
        }

        //вызывать парсинг из последнего блока в выходные переменные воркфлоу
        valueContextManager.start(answerContainer, context.getScenarioId(), new VariableCallback() {
            @Override
            public void loaded(ValueContainer loadedValues) {
                context.lockAndLoad();
                context.setOutValues(loadedValues);
                context.setState(ContextState.EXECUTED);
                context.unlockAndSave();
                operationService.executeState(context);

            }

            @Override
            public void error(Exception e) {
                context.lockAndLoad();
                context.saveError(e);
                operationService.executeState(context);
            }
        });
    }

}
