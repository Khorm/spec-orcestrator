package com.petra.lib.constructor.model;

public class RemoteConsumerModel {
    private Long id;
    private String version;
    private String serviceName;

    private Long workflowId;

    private String workflowVersion;

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
}
