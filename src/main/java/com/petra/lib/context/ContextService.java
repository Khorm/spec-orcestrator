package com.petra.lib.context;

import com.petra.lib.context.block.BlockContextImpl;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.context.block.repo.ContextRepo;
import com.petra.lib.context.workflow.repo.WorkflowContextRepo;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.context.workflow.WorkflowContextImpl;
import com.petra.lib.transaction.TransactionManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ContextService {
    ContextRepo contextRepo;
    WorkflowContextRepo workflowContextRepo;

    public Context createContext(Identifier blockId, UUID scenarioId) {
        return new BlockContextImpl(contextRepo,
                blockId, scenarioId);
    }

    public Context createContext(ContextEntity contextEntity) {
        return new BlockContextImpl(contextEntity, contextRepo);
    }

    public WorkflowContext createWorkflowContext(UUID scenarioId, Identifier workflowId) {
        return new WorkflowContextImpl(workflowContextRepo, scenarioId, workflowId);
    }
}
