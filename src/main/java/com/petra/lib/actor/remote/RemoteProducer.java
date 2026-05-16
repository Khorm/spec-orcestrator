package com.petra.lib.actor.remote;

import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;

public final class RemoteProducer {
    private final Identifier producerId;
    private final String serviceName;
    private final ValueContainer sendValuesContainer;
    private final Identifier consumerId;

    public RemoteProducer(Identifier producerId, String serviceName,
                          ValueContainer sendValuesContainer, Identifier consumerId) {
        this.producerId = producerId;
        this.serviceName = serviceName;
        this.sendValuesContainer = sendValuesContainer;
        this.consumerId = consumerId;
    }

    public Identifier getProducerId() {
        return producerId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Long getId() {
        return producerId.getId();
    }

    public String getVersion() {
        return producerId.getVersion();
    }

    public ValueContainer getSendValuesContainer() {
        return sendValuesContainer.clone();
    }

    public Identifier getConsumerId() {
        return consumerId;
    }


}
