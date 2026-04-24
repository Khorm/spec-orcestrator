package com.petra.lib.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.actor.LocalProducer;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.operation.OperationService;
import com.petra.lib.actor.LocalConsumer;
import com.petra.lib.actor.RemoteProducer;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueDto;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public class BlockContextExecutor {
    private final TransactionManager transactionManager;
    private final OperationService workflowOperationService;
    private final OperationService actionOperationService;
    private final OperationService userOperationService;
    private final Map<Identifier, LocalConsumer> consumerMap;
    private final ContextService contextService;


    public BlockContextExecutor(TransactionManager transactionManager,
                                OperationService workflowOperationService, OperationService userOperationService,
                                Collection<LocalConsumer> consumers,
                                OperationService actionOperationService,
                                ContextService contextService) {
        this.transactionManager = transactionManager;
        this.workflowOperationService = workflowOperationService;
        this.userOperationService = userOperationService;
        this.actionOperationService = actionOperationService;
        this.consumerMap = consumers.stream().collect(Collectors.toMap(LocalConsumer::getIdentifier, Function.identity()));
        this.contextService = contextService;
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

        log.info("Workflow starting by User: {}, id={}", workflowName, scenarioID);
        Context context = contextService.createContext(executingConsumer.getIdentifier(), scenarioID);
        ValueContainer outContainer = ValueContainerFactory.getSimpleContainer(executingConsumer.getOutputVariables());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, ValueModel> consumerValues = executingConsumer.getInputVariables().stream()
                .collect(Collectors.toMap(ValueModel::getName, Function.identity()));
        Collection<ValueDto> values = new ArrayList<>(params.size());
        params.forEach((s, o) -> {
            try {
                ValueModel thisValue = consumerValues.get(s);
                values.add(new ValueDto(thisValue.getId(), s, thisValue.getMultiplicityEnm(), mapper.writeValueAsString(o)));
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
        boolean isNewCreated;
        try(Transaction transaction = transactionManager.createNewTransaction(false, null)) {
            isNewCreated = context.insert(remoteProducer, executingConsumer.getBlockType(),
                    ContextState.STARTED, outContainer, transaction);

        }catch (Exception e){
            throw new RuntimeException(e);
        }
        if (!isNewCreated) {
            throw new IllegalArgumentException();
        }
        userOperationService.executeState(context);
    }

    public boolean startContext(UUID scenarioId, RemoteProducer remoteProducer) {
        //выгружает контекст из базы и запускает обработку
        LocalConsumer consumer = consumerMap.get(remoteProducer.getConsumerId());
        Context context = contextService.createContext(consumer.getIdentifier(), scenarioId);
        return startContext(context, consumer , remoteProducer);
    }

    public boolean startContext(ContextEntity entity){
        LocalConsumer consumer = consumerMap.get(entity.getConsumerId());
        RemoteProducer producer = entity.getProducer();
        Context context = contextService.createContext(entity);
        return startContext(context, consumer,producer);
    }

    private boolean startContext(Context context, LocalConsumer consumer, RemoteProducer remoteProducer) {
        try(Transaction transaction = transactionManager.createNewTransaction(false, null)) {
            ValueContainer outContainer = ValueContainerFactory.getSimpleContainer(consumer.getOutputVariables());
            boolean isNewCreated = context.insert(remoteProducer, consumer.getBlockType(),
                    ContextState.STARTED, outContainer, transaction);

            context.lockAndLoad(transaction);
            if (context.getCurrentState() != ContextState.STARTED) {
                transaction.rollback();
                log.info("[{}] Repeating {} - {}", context.getScenarioId(), consumer.getName(), consumer.getBlockType());
                return false;
            }

            log.info("[{}] Starting {} - {}", context.getScenarioId(), consumer.getName(), consumer.getBlockType());
            if (consumer.getBlockType() == BlockType.ACTION) {
                actionOperationService.executeState(context);
            } else if (consumer.getBlockType() == BlockType.WORKFLOW) {
                workflowOperationService.executeState(context);
            } else {
                transaction.rollback();
                throw new IllegalStateException("Wrong block type " + consumer.getBlockType());
            }
            transaction.commit();
            return true;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }




}
