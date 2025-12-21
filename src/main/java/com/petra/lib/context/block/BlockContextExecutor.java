package com.petra.lib.context.block;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalConsumer;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.operation.ActivityOperationService;
import com.petra.lib.operation.WorkflowOperationService;
import com.petra.lib.variable.container.ValueContainer;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockContextExecutor {
    private final ContextRepo contextRepo;
    private final WorkflowOperationService workflowOperationService;
    private final ActivityOperationService actionOperationService;
    private final Map<Identifier, LocalConsumer> consumerMap;
    private final Map<Identifier, LocalProducer> producerMap;



    public BlockContextExecutor(ContextRepo contextRepo,
                                WorkflowOperationService workflowOperationService,
                                ActivityOperationService actionOperationService,
                                Collection<LocalConsumer> consumers,
                                Collection<LocalProducer> producers) {
        this.contextRepo = contextRepo;
        this.workflowOperationService = workflowOperationService;
        this.actionOperationService = actionOperationService;
        this.consumerMap = consumers.stream().collect(Collectors.toMap(LocalConsumer::getIdentifier, Function.identity()));
        this.producerMap = producers.stream().collect(Collectors.toMap(LocalProducer::getWorkflowId, Function.identity()));
    }

    public void startContext(UUID scenarioId, RemoteProducer remoteProducer) {
        //выгружает контекст из базы и запускает обработку
        LocalConsumer consumer = consumerMap.get(remoteProducer.getConsumerId());
        Optional<ContextEntity> optionalEntity = contextRepo.findContext(scenarioId, consumer.getIdentifier());
        Context context;
        if (optionalEntity.isEmpty()) {
            BlockType type = consumerMap.containsKey(remoteProducer.getConsumerId()) ? BlockType.ACTION : BlockType.WORKFLOW;
            ContextEntity entity = new ContextEntity(scenarioId, remoteProducer, type, ContextState.STARTED, remoteProducer.getSendValuesContainer());
            context = new BlockContextImpl(entity, contextRepo,
                    type == BlockType.ACTION ? actionOperationService : workflowOperationService);
        }else {
            context = new BlockContextImpl(optionalEntity.get(), contextRepo,
                    optionalEntity.get().getBlockType() == BlockType.ACTION ? actionOperationService : workflowOperationService);
        }

        context.save();
    }


//    public void loadContext(UUID scenarioId, Identifier consumerId, ValueContainer newOutputValues) {
//        LocalConsumer consumer = consumerMap.get(consumerId);
//        Optional<ContextEntity> optionalEntityOpt = contextRepo.findContext(scenarioId, consumer.getConsumerId());
//        if (optionalEntityOpt.isEmpty()) {
//            throw new NullPointerException("Context not found " + consumer.getName());
//        }
//
//        ContextEntity entity = optionalEntityOpt.get();
//        entity.setOutContextValues(newOutputValues);
//        operationService.executeState(createContext(entity, consumer));
//    }

    public void handleAnswerFromBlock(ValueContainer outputValues, Identifier answeredBlockId,
                                      UUID scenarioId, Identifier workflowId) {
        Optional<ContextEntity> optionalEntity = contextRepo.findContext(scenarioId, workflowId);
        if (optionalEntity.isPresent()) {
            Context context = new BlockContextImpl(optionalEntity.get(), contextRepo,
                    optionalEntity.get().getBlockType() == BlockType.ACTION ? actionOperationService : workflowOperationService);
            LocalProducer localProducer = producerMap.get(workflowId);
            localProducer.answerFromBlock(outputValues, answeredBlockId, context);
        }
        throw new NullPointerException("Context not found " + workflowId);

    }

//    private Context createContext(ContextEntity entity, LocalConsumer localConsumer) {
//        return new BlockContextImpl(
//                entity,
//                operationService,
//                transactionManager,
//                contextRepo,
//                localConsumer);
//    }
}
