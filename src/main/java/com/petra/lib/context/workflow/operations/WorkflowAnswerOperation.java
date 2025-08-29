package com.petra.lib.context.workflow.operations;

import com.petra.lib.context.ContextState;
import com.petra.lib.context.block.BlockContextExecutor;
import com.petra.lib.context.operation.Operation;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContext;
import org.springframework.transaction.annotation.Isolation;

public class WorkflowAnswerOperation implements Operation<WorkflowContext> {
    private final TransactionManager transactionManager;
    private final BlockContextExecutor blockContextExecutor;
    private final ContextState CURRENT_STATE = ContextState.EXECUTED;

    public WorkflowAnswerOperation(TransactionManager transactionManager,
                                   BlockContextExecutor blockContextExecutor) {
        this.transactionManager = transactionManager;
        this.blockContextExecutor = blockContextExecutor;
    }

    @Override
    public void execute(WorkflowContext context) {
        transactionManager.executeInTransaction(transaction -> {
            context.setState(context.getWorkflowOutputValues(), CURRENT_STATE);

            if (context.getNextConsumer().isPresent()) {
                context.getExecutor().loadContext(context);
            } else {
                ValueContext valueContext = new ValueContext(context.getWorkflowOutputValues(), context.getLastWorkflowBlockOuterParser(),
                        context.getScenarioId(), new VariableCallback() {
                    @Override
                    public void loaded(ValueContainer loadedValues) {
                        blockContextExecutor.loadContext(context.getScenarioId(),
                                context.getRemoteConsumer().getIdentifier(), loadedValues);
                    }

                    @Override
                    public void error(Exception e) {
                        context.error(e);
                    }
                });
                valueContext.start();

            }

        }, Isolation.SERIALIZABLE);
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
