package com.petra.lib.context.block.operations;

import com.petra.lib.context.ContextState;
import com.petra.lib.context.block.BlockContext;
import com.petra.lib.context.operation.Operation;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.context.ValueContext;

public final class ValueParsingOperation implements Operation<BlockContext> {

    private final ContextState CURRENT_STATE = ContextState.STARTED;

    @Override
    public void execute(BlockContext blockContext) {
        ValueContainer valueContainer = ValueContainerFactory.getSimpleContainer(blockContext.getProducer()
                .getValuesContainer().getJson());
        ValueContext valueContext = new ValueContext(ValueContainerFactory.getImmutableContainer(valueContainer),
                blockContext.getValueContextModel(),
                blockContext.getScenarioId(),
                new VariableCallback() {
                    @Override
                    public void loaded(ValueContainer loadedValues) {
                        valueContainer.mixinValueContainer(valueContainer);
                        blockContext.setState(valueContainer, CURRENT_STATE);
                    }

                    @Override
                    public void error(Exception e) {
                        blockContext.error(e);
                    }
                });
        valueContext.start();
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
