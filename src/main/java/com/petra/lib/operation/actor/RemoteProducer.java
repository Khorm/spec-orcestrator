package com.petra.lib.operation.actor;

import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;

public final class RemoteProducer {
    private final Identifier identifier;
    private final String serviceName;
    private final ValueContainer sendValuesContainer;
    private final Identifier consumerId;

    public RemoteProducer(Identifier identifier, String serviceName,
                          ValueContainer sendValuesContainer, Identifier consumerId) {
        this.identifier = identifier;
        this.serviceName = serviceName;
        this.sendValuesContainer = sendValuesContainer;
        this.consumerId = consumerId;
    }

    public Identifier getIdentifier() {
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

    public ValueContainer getSendValuesContainer() {
        return sendValuesContainer.clone();
    }

    public Identifier getConsumerId() {
        return consumerId;
    }


}
