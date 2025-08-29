package com.petra.lib.context.model;

import java.util.Objects;

public class ModelIdentifier {
    private final Identifier identifier;
    private final Long workflowId;

    public ModelIdentifier(Long id, String version, Long workflowId) {
        this.identifier = new Identifier(id, version);
        this.workflowId = workflowId;
    }

    public Long getId() {
        return identifier.getId();
    }

    public String getVersion(){
        return identifier.getVersion();
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelIdentifier)) return false;
        ModelIdentifier that = (ModelIdentifier) o;
        return Objects.equals(identifier, that.identifier) && Objects.equals(workflowId, that.workflowId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, workflowId);
    }
}
