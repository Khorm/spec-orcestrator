package com.petra.lib.context.repo;


import com.petra.lib.context.model.ConsumerIdentifier;
import com.petra.lib.context.workflow.WorkflowContextEntity;

import java.util.Optional;
import java.util.UUID;

class WorkflowContextRepoImpl implements WorkflowContextRepo{
    @Override
    public Optional<WorkflowContextEntity> getWorkflowContext(ConsumerIdentifier consumerIdentifier, UUID scenarioId) {

    }

    @Override
    public boolean insertContext(WorkflowContextEntity contextEntity) {

    }

    @Override
    public boolean updateContext(WorkflowContextEntity contextEntity) {

    }
}
