package com.petra.lib.context.repo;

import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.ConsumerIdentifier;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.context.model.RemoteConsumer;
import com.petra.lib.context.workflow.WorkflowContextEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowContextRepo {

//    Collection<WorkflowContextEntity> getWorkflowContexts(LocalProducer localProducer, UUID scenarioId);

    Optional<WorkflowContextEntity> getWorkflowContext(ConsumerIdentifier consumerIdentifier, UUID scenarioId);
    boolean insertContext(WorkflowContextEntity contextEntity);

    //TODO: заблокирвать запись перед апдейтом
    boolean updateContext(WorkflowContextEntity contextEntity);
//
//    ContextState getState();
}
