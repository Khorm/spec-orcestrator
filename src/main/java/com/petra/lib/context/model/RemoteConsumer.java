package com.petra.lib.context.model;

import com.petra.lib.constructor.model.RemoteConsumerModel;

public class RemoteConsumer {
    private final ModelIdentifier id;
    private final String serviceName;

    public RemoteConsumer(RemoteConsumerModel remoteConsumerModel) {
        this.id = new ModelIdentifier(remoteConsumerModel.getId(), remoteConsumerModel.getVersion(), remoteConsumerModel.getWorkflowId());
        this.serviceName = remoteConsumerModel.getServiceName();
    }

    public ModelIdentifier getIdentifier() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }
}
