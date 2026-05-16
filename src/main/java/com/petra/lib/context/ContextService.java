package com.petra.lib.context;

import com.petra.lib.actor.remote.RemoteProducer;
import com.petra.lib.context.block.ActivityContextImpl;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.context.block.repo.ContextRepo;
import com.petra.lib.context.condition.ConditionContext;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.context.workflow.WorkflowContextImpl;
import com.petra.lib.context.workflow.repo.WorkflowContextRepo;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ContextService {
    ContextRepo contextRepo;
    WorkflowContextRepo workflowContextRepo;
    TransactionManager transactionManager;
    String thisServiceName;

//    public Context fillDefaultContext(Identifier blockId, UUID scenarioId) {
//        return new ActivityContextImpl(contextRepo,
//                blockId, scenarioId);
//    }
//
//    public Context fillDefaultContext(ContextEntity contextEntity) {
//        return new ActivityContextImpl(contextEntity, contextRepo);
//    }
//
//    public WorkflowContext fillDefaultWorkflowContext(UUID scenarioId, Identifier workflowId) {
//        return new WorkflowContextImpl(workflowContextRepo, scenarioId, workflowId);
//    }

    public Optional<WorkflowContext> createWorkflowContext(Identifier workflowId, UUID scenarioId, ValueContainer inputValues){
        WorkflowContext workflowContext = new WorkflowContextImpl(workflowContextRepo, scenarioId, workflowId);
        Boolean result = transactionManager.executeInTransaction(transaction -> {
            return workflowContext.create(inputValues, transaction);
        });

        return result ? Optional.of(workflowContext) : Optional.empty();
    }

    public WorkflowContext loadWorkflowContext(Identifier workflowId, UUID scenarioId){
        WorkflowContext workflowContext = new WorkflowContextImpl(workflowContextRepo, scenarioId, workflowId);
        transactionManager.executeInTransactionReadOnly(workflowContext::load);
        return workflowContext;
    }


    public Optional<Context> createBlockContext(Identifier blockId, UUID scenarioId, RemoteProducer remoteProducer,
                                                BlockType blockType) {
        ActivityContextImpl context = new ActivityContextImpl(contextRepo,
                blockId, scenarioId);
        Boolean result = transactionManager.executeInTransaction(transaction -> {
            return context.insert(remoteProducer, blockType, ContextState.STARTED, transaction);
        });

        return result ? Optional.of(context) : Optional.empty();

    }

    public Context loadContext(Identifier blockId, UUID scenarioId) {
        ActivityContextImpl context = new ActivityContextImpl(contextRepo,
                blockId, scenarioId);
        transactionManager.executeInTransactionReadOnly(context::load);
        return context;
    }

    public Context createConditionContext(UUID scenarioID, RemoteProducer remoteProducer,
                                          Identifier conditionId) {
        return new ConditionContext(scenarioID, remoteProducer, remoteProducer.getSendValuesContainer(), conditionId);
    }

//    public Optional<Context> createBlockWorkflowContext(Identifier blockId, UUID scenarioId, RemoteProducer remoteProducer) {
//        ActivityContextImpl context = new ActivityContextImpl(contextRepo,
//                blockId, scenarioId);
//        Boolean result = transactionManager.executeInTransaction(transaction -> {
//            return context.insert(remoteProducer, BlockType.WORKFLOW, ContextState.STARTED, transaction);
//        });
//
//        return result ? Optional.of(context) : Optional.empty();
//
//    }

    public Context createSourceContext(UUID scenarioID, RemoteProducer remoteProducer,
                                       Identifier sourceId) {
        return new ConditionContext(scenarioID, remoteProducer, remoteProducer.getSendValuesContainer(), sourceId);
    }

    public Collection<Context> getNotFinishedContexts(int limitTimeInSeconds) {
        Collection<ContextEntity> contextEntities = transactionManager
                .executeInTransactionReadOnly((Function<Transaction, Collection<ContextEntity>>)
                        transaction -> contextRepo.getNotFinishedContexts(thisServiceName,
                                limitTimeInSeconds, transaction));

        Collection<Context> results = new ArrayList<>();
        for (ContextEntity contextEntity : contextEntities){
            results.add(new ActivityContextImpl(contextEntity, contextRepo));
        }
        return results;

    }
}
