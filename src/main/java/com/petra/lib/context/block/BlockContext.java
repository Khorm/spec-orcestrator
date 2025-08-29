package com.petra.lib.context.block;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.model.LocalConsumer;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextModel;

import java.util.UUID;

public interface BlockContext extends Context {

    UUID getScenarioId();

    RemoteProducer getProducer();

    LocalConsumer getConsumer();

    ContextState getContextState();

    ValueContainer getContextInputContainer();
    ValueContainer getContextOutputContainer();

    void run();

    ExecutionStatus getExecutionStatus();

    void setState(ValueContainer values, ContextState executedState);

    void error(Exception e);

    ValueContextModel getValueContextModel();
}
