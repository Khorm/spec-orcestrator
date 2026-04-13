package com.petra.lib.context.workflow.repo;

import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.utils.id.Identifier;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowContextRepo {
    boolean insertContext(WorkflowContextEntity context, Transaction transaction);

    Optional<WorkflowContextEntity> findContext(UUID scenarioId, Identifier workflowId, Transaction transaction, boolean isLock);
    Optional<WorkflowContextEntity> findFinishedContext(UUID scenarioId);

    void save(WorkflowContextEntity entity, Transaction transaction);

}
