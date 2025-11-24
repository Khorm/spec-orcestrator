package com.petra.lib.context.block;

import com.petra.lib.context.Context;
import com.petra.lib.context.model.*;
import com.petra.lib.context.ContextState;
import com.petra.lib.operation.OperationService;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.container.ValueContainer;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockContextExecutor {
    private final TransactionManager transactionManager;
    private final ContextRepo contextRepo;
    private final OperationService operationService;
    private final Map<Identifier, LocalConsumer> consumerMap;
    private final Map<Identifier, LocalProducer> producerMap;


    public BlockContextExecutor(TransactionManager transactionManager, ContextRepo contextRepo,
                                OperationService operationService, Collection<LocalConsumer> consumers,
                                Map<Identifier, LocalProducer> producerMap) {
        this.transactionManager = transactionManager;
        this.contextRepo = contextRepo;
        this.operationService = operationService;
        this.consumerMap = consumers.stream().collect(Collectors.toMap(LocalConsumer::getConsumerId, Function.identity()));
        this.producerMap = producerMap;
    }

    public void startContext(UUID scenarioId, RemoteProducer remoteProducer) {
        //выгружает контекст из базы и запускает обработку
        LocalConsumer consumer = consumerMap.get(remoteProducer.getConsumerId());
        Optional<ContextEntity> optionalEntity = contextRepo.findContext(scenarioId, consumer.getIdentifier().getConsumerId());
        ContextEntity contextEntity = optionalEntity.orElseGet(() -> new ContextEntity(scenarioId,
                remoteProducer,
                ContextState.STARTED));
        Context context = createContext(contextEntity, consumer);
        context.save();
        operationService.executeState(context);
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

    public void handleAnswerFromBlock(ValueContainer outputValues,Identifier answeredBlockId,
                                    UUID scenarioId, Identifier workflowId){
        LocalProducer localProducer = producerMap.get(workflowId);
        localProducer.answerFromBlock(outputValues, answeredBlockId, scenarioId);
    }

    private Context createContext(ContextEntity entity, LocalConsumer localConsumer) {
        return new BlockContextImpl(
                entity,
                operationService,
                transactionManager,
                contextRepo,
                localConsumer);
    }
}
