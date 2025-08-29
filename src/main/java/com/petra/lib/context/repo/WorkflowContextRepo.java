package com.petra.lib.context.repo;

import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.context.model.RemoteConsumer;
import com.petra.lib.context.workflow.WorkflowContextEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowContextRepo {

    Collection<WorkflowContextEntity> getWorkflowContexts(LocalProducer localProducer, UUID scenarioId);

    Optional<WorkflowContextEntity> getWorkflowContext(LocalProducer localProducer, RemoteConsumer remoteConsumer, UUID scenarioId);
    void insertContext(WorkflowContextEntity contextEntity);

    void updateContext(WorkflowContextEntity contextEntity);

    ContextState getState();
}
