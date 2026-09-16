package com.petra.lib.actor.remote.consumer;

import com.petra.lib.PetraException;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.operation.OperationService;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RemoteEnd implements RemoteConsumer {
    ValueContextManager valueContextExitManager;
    ContextService contextService;
    Identifier workflowId;
    String workflowName;
    TransactionManager transactionManager;
    OperationService operationService;


    @Override
    public void handleAnswer(LinkedConsumerListMessage message) {
        throw new PetraException("Not supported for start");
    }

    @Override
    public void execute(LinkedConsumerListMessage message) {
        Context context = contextService.loadContext(workflowId, message.getScenarioId());
        log.info("{} workflow {} finished with success={}", context.getScenarioId(), workflowName, true);


        //вызывать парсинг из последнего блока в выходные переменные воркфлоу
        valueContextExitManager.start(message.getWorkflowContextVariables(), context.getScenarioId(), new VariableCallback() {
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

                    WorkflowContext wfCtx = contextService.loadWorkflowContext(workflowId, context.getScenarioId());
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

                    WorkflowContext wfCtx = contextService.loadWorkflowContext(workflowId, context.getScenarioId());
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
