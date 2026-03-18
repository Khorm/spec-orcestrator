package com.petra.lib.context.block;

import com.petra.lib.context.Context;
import com.petra.lib.context.model.ConsumerIdentifier;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.context.repo.WorkflowContextRepo;
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
    TransactionManager transactionManager;
    ContextRepo contextRepo;
    WorkflowContextRepo workflowContextRepo;

    public Context createContext(Identifier blockId, UUID scenarioId) {
        return new BlockContextImpl(contextRepo,
                blockId, scenarioId, transactionManager);
    }

    public Context createContext(ContextEntity contextEntity) {
        return new BlockContextImpl(contextEntity, contextRepo,
                transactionManager);
    }

    public WorkflowContext createWorkflowContext(UUID scenarioId, ConsumerIdentifier consumerIdentifier) {
        return new WorkflowContextImpl(workflowContextRepo, transactionManager,
                scenarioId, consumerIdentifier);
    }
}
