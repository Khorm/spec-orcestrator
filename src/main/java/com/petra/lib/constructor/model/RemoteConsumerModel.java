package com.petra.lib.constructor.model;

import java.util.Collection;

public class RemoteConsumerModel {
    private Long id;
    private String version;
    private String serviceName;

    private Long workflowId;

    private String workflowVersion;
    private ValuesCollectionModel blockValues;


    public Long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public String getWorkflowVersion() {
        return workflowVersion;
    }

    public ValuesCollectionModel getBlockValues() {
        return blockValues;
    }
}
