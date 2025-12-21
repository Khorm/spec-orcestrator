package com.petra.lib.context;

import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.variable.container.ValueContainer;

import java.util.UUID;

public interface Context {
    void setState(ContextState contextState);

    void setOutValues(ValueContainer values);

    ContextState getCurrentState();

    UUID getScenarioId();

    RemoteProducer getProducer();

    ValueContainer getContextOutValues();

    ValueContainer getContextInputValues();

    Identifier getCurrentBlockId();

    ExecutionStatus getExecutionStatus();

    String getProducerServiceName();

    void save();
    void saveError(Exception e);
}
