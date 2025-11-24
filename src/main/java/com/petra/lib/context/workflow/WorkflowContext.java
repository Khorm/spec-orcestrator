package com.petra.lib.context.workflow;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.context.model.RemoteConsumer;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextModel;

import java.util.Optional;
import java.util.UUID;

@Deprecated
interface WorkflowContext extends Context {
    UUID getScenarioId();

    void setState(ValueContainer values, ContextState executedState);

    LocalProducer getLocalProducer();

    RemoteConsumer getRemoteConsumer();

    ValueContainer getWorkflowInputValues();

    ValueContainer getWorkflowOutputValues();

    Optional<RemoteConsumer> getNextConsumer();

    ValueContextModel getLastWorkflowBlockOuterParser();


//    WorkflowContextExecutor getExecutor();
}
