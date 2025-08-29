package com.petra.lib.context.model;

import com.petra.lib.variable.container.ValueContainer;

public final class RemoteProducer {
    private final ModelIdentifier identifier;
    private final String serviceName;
    private final ValueContainer valuesContainer;
    private final ModelIdentifier consumerId;

    public RemoteProducer(ModelIdentifier identifier, String serviceName, ValueContainer valuesContainer, ModelIdentifier consumerId) {
        this.identifier = identifier;
        this.serviceName = serviceName;
        this.valuesContainer = valuesContainer;
        this.consumerId = consumerId;
    }

    public ModelIdentifier getIdentifier() {
        return identifier;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Long getId() {
        return identifier.getId();
    }

    public String getVersion() {
        return identifier.getVersion();
    }

    public ValueContainer getValuesContainer() {
        return valuesContainer.clone();
    }

    public ModelIdentifier getConsumerId() {
        return consumerId;
    }
}
