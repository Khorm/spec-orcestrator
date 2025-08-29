package com.petra.lib.context.block;

import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.LocalConsumer;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.context.operation.OperationServiceImpl;
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
    private final OperationServiceImpl<BlockContext> operationService;
    private final Map<Identifier, LocalConsumer> consumerMap;


    public BlockContextExecutor(TransactionManager transactionManager, ContextRepo contextRepo,
                                OperationServiceImpl<BlockContext> operationService, Collection<LocalConsumer> consumers) {
        this.transactionManager = transactionManager;
        this.contextRepo = contextRepo;
        this.operationService = operationService;
        this.consumerMap = consumers.stream().collect(Collectors.toMap(LocalConsumer::getIdentifier, Function.identity()));

    }

    public void startContext(UUID scenarioId, RemoteProducer remoteProducer) {
        //выгружает контекст из базы и запускает обработку
        LocalConsumer consumer = consumerMap.get(remoteProducer.getConsumerId());
        Optional<ContextEntity> optionalEntity = contextRepo.findContext(scenarioId, consumer.getIdentifier());
        ContextEntity contextEntity = optionalEntity.orElseGet(() -> new ContextEntity(scenarioId,
                remoteProducer,
                consumer.getIdentifier(),
                ContextState.CREATED));
        createContext(contextEntity, consumer).run();
    }

    public void loadContext(UUID scenarioId, Identifier consumerId, ValueContainer newOutputValues) {
        LocalConsumer consumer = consumerMap.get(consumerId);
        Optional<ContextEntity> optionalEntityOpt = contextRepo.findContext(scenarioId, consumer.getIdentifier());
        if (optionalEntityOpt.isEmpty()) {
            throw new NullPointerException("Context not found " + consumer.getName());
        }

        ContextEntity entity = optionalEntityOpt.get();
        entity.setOutContextValues(newOutputValues);
        createContext(entity, consumer).run();
    }

    private BlockContextImpl createContext(ContextEntity entity, LocalConsumer localConsumer) {
        return new BlockContextImpl(
                entity,
                operationService,
                transactionManager,
                contextRepo,
                localConsumer);
    }
}
