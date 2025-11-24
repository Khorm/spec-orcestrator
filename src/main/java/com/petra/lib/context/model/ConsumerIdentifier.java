package com.petra.lib.context.model;

import java.util.Objects;

public class ConsumerIdentifier {
    private final Identifier consumerId;
    private final Identifier workflowId;

    public ConsumerIdentifier(Long id, String version, Long workflowId, String workflowVersion) {
        this.consumerId = new Identifier(id, version);
        this.workflowId = new Identifier(workflowId, workflowVersion);
    }

    public ConsumerIdentifier(Identifier consumerId, Identifier workflowId) {
        this.consumerId = consumerId;
        this.workflowId = workflowId;
    }

    public Identifier getConsumerId() {
        return consumerId;
    }

    public Identifier getWorkflowId() {
        return workflowId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsumerIdentifier)) return false;
        ConsumerIdentifier that = (ConsumerIdentifier) o;
        return Objects.equals(getConsumerId(), that.getConsumerId()) && Objects.equals(getWorkflowId(), that.getWorkflowId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConsumerId(), getWorkflowId());
    }
}
