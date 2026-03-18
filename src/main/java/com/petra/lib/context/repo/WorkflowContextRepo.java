package com.petra.lib.context.repo;

import com.petra.lib.context.model.ConsumerIdentifier;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.transaction.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowContextRepo {
    boolean insertContext(WorkflowContextEntity context, Transaction transaction);


    Optional<WorkflowContextEntity> findContext(UUID scenarioId, ConsumerIdentifier consumerId, Transaction transaction);

    void save(WorkflowContextEntity entity, Transaction transaction);
}
