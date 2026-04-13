package com.petra.lib.context.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.context.block.repo.ContextRepo;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.operation.OperationService;
import com.petra.lib.operation.actor.LocalConsumer;
import com.petra.lib.operation.actor.RemoteProducer;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueDto;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
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
    private final OperationService userOperationService;
    private final Map<Identifier, LocalConsumer> consumerMap;
    private final ContextService contextService;


    public BlockContextExecutor(ContextRepo contextRepo,
                                OperationService workflowOperationService, OperationService userOperationService,
                                Collection<LocalConsumer> consumers,
                                OperationService actionOperationService,
                                ContextService contextService) {
        this.contextRepo = contextRepo;
        this.workflowOperationService = workflowOperationService;
        this.userOperationService = userOperationService;
        this.actionOperationService = actionOperationService;
        this.consumerMap = consumers.stream().collect(Collectors.toMap(LocalConsumer::getIdentifier, Function.identity()));
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


    public void startWorkflowByUser(String workflowName, String version, Map<String, Object> params) {
        UUID scenarioId = UUID.randomUUID();
        startWorkflowByUser(workflowName, version, params, scenarioId);
    }

    public void startWorkflowByUser(String workflowName, String version, Map<String, Object> params, UUID scenarioID) {
        LocalConsumer executingConsumer = null;
        for (Map.Entry<Identifier, LocalConsumer> entry : consumerMap.entrySet()) {
            if (entry.getKey().getVersion().equals(version) && entry.getValue().getName().equals(workflowName)) {
                executingConsumer = entry.getValue();
                break;
            }
        }
        if (executingConsumer == null) throw new RuntimeException("No such Workflow");


        Context context = contextService.createContext(executingConsumer.getIdentifier(), scenarioID);
        ValueContainer outContainer = ValueContainerFactory.getSimpleContainer(executingConsumer.getOutputVariables());

        ObjectMapper mapper = new ObjectMapper();
        Collection<ValueDto> values = new ArrayList<>(params.size());
        params.forEach((s, o) -> {
            try {
                values.add(new ValueDto(null, s, null, mapper.writeValueAsString(o)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        RemoteProducer remoteProducer = new RemoteProducer(
                new Identifier(-1L, "NONE"),
                "USER",
                ValueContainerFactory.getSimpleContainer(values),
                executingConsumer.getIdentifier()
        );
        boolean isNewCreated = context.create(remoteProducer, executingConsumer.getBlockType(),
                ContextState.STARTED, outContainer);
        if (!isNewCreated) {
            throw new IllegalArgumentException();
        }
        userOperationService.executeState(context);
    }

    public boolean startContext(UUID scenarioId, RemoteProducer remoteProducer) {
        //выгружает контекст из базы и запускает обработку
        LocalConsumer consumer = consumerMap.get(remoteProducer.getConsumerId());
        Context context = contextService.createContext(consumer.getIdentifier(), scenarioId);

        ValueContainer outContainer = ValueContainerFactory.getSimpleContainer(consumer.getOutputVariables());
        boolean isNewCreated = context.create(remoteProducer, consumer.getBlockType(), ContextState.STARTED, outContainer);
//        if (!isNewCreated) {
//            context.load();
//        }
        context.lockAndLoad();
        if (context.getCurrentState() != ContextState.STARTED){
            return false;
        }

        if (consumer.getBlockType() == BlockType.ACTION) {
            actionOperationService.executeState(context);
        } else if (consumer.getBlockType() == BlockType.WORKFLOW) {
            workflowOperationService.executeState(context);
        } else {
            throw new IllegalStateException("Wrong block type " + consumer.getBlockType());
        }
        return true;

    }


}
