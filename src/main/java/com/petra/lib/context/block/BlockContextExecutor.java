package com.petra.lib.context.block;

import com.petra.lib.context.Context;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalConsumer;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.operation.OperationService;
import com.petra.lib.variable.container.ValueContainer;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public class BlockContextExecutor {
    private final ContextRepo contextRepo;
    private final OperationService workflowOperationService;
    private final OperationService actionOperationService;
    private final Map<Identifier, LocalConsumer> consumerMap;
    private final Map<Identifier, LocalProducer> producerMap;
    private final ContextService contextService;


    public BlockContextExecutor(ContextRepo contextRepo,
                                OperationService workflowOperationService,
                                Collection<LocalConsumer> consumers,
                                OperationService actionOperationService,
                                Collection<LocalProducer> producers, ContextService contextService) {
        this.contextRepo = contextRepo;
        this.workflowOperationService = workflowOperationService;
        this.actionOperationService = actionOperationService;
        this.consumerMap = consumers.stream().collect(Collectors.toMap(LocalConsumer::getIdentifier, Function.identity()));
        this.producerMap = producers.stream().collect(Collectors.toMap(LocalProducer::getWorkflowId, Function.identity()));
        this.contextService = contextService;
    }

    public void start() {
        consumerMap.values().stream()
                .map(localConsumer -> {
                    ContextEntity entity = contextRepo.getNotFinishedContexts(localConsumer.getIdentifier());
                    return contextService.createContext(entity);
                })
                .forEach(context -> {
                    if (context.getBlockType() == BlockType.ACTION) {
                        actionOperationService.executeState(context);
                    } else if (context.getBlockType() == BlockType.WORKFLOW) {
                        workflowOperationService.executeState(context);
                    } else {
                        throw new IllegalStateException("Wrong block type " + context.getBlockType());
                    }
                });

    }

    public void startContext(UUID scenarioId, RemoteProducer remoteProducer) {
        //выгружает контекст из базы и запускает обработку
        LocalConsumer consumer = consumerMap.get(remoteProducer.getConsumerId());
        Context context = contextService.createContext(consumer.getIdentifier(), scenarioId);
        boolean isNewCreated = context.create();
        if (!isNewCreated) {
            context.load();
        }
        BlockType type = consumerMap.get(remoteProducer.getConsumerId()).getBlockType();
        if (type == BlockType.ACTION) {
            actionOperationService.executeState(context);
        } else if (type == BlockType.WORKFLOW) {
            workflowOperationService.executeState(context);
        } else {
            throw new IllegalStateException("Wrong block type " + type);
        }

    }


    public void handleAnswerFromBlock(ValueContainer outputValues, Identifier answeredBlockId,
                                      UUID scenarioId, Identifier workflowId, ExecutionStatus executionStatus) {

        LocalProducer localProducer = producerMap.get(workflowId);
        localProducer.answerFromBlock(outputValues, answeredBlockId, scenarioId, executionStatus);

    }


}
