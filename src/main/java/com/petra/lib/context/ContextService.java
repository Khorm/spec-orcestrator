package com.petra.lib.context;

import com.petra.lib.context.block.BlockContextImpl;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.context.block.repo.ContextRepo;
import com.petra.lib.context.workflow.repo.WorkflowContextRepo;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.context.workflow.WorkflowContextImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ContextService {
    ContextRepo contextRepo;
    WorkflowContextRepo workflowContextRepo;

    public Context fillDefaultContext(Identifier blockId, UUID scenarioId) {
        return new BlockContextImpl(contextRepo,
                blockId, scenarioId);
    }

    public Context fillDefaultContext(ContextEntity contextEntity) {
        return new BlockContextImpl(contextEntity, contextRepo);
    }

    public WorkflowContext fillDefaultWorkflowContext(UUID scenarioId, Identifier workflowId) {
        return new WorkflowContextImpl(workflowContextRepo, scenarioId, workflowId);
    }
}
