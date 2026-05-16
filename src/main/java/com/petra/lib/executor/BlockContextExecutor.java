package com.petra.lib.executor;

import com.petra.lib.actor.local.LocalConsumer;
import com.petra.lib.actor.remote.RemoteProducer;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.operation.OperationService;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

import java.util.*;

@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BlockContextExecutor {
    OperationService asyncOperationService;
    ConsumerCollection consumerCollection;
    OperationService syncOperationService;
    ContextService contextService;
    OperationService userOperationService;

    public MessageResponse start(UUID scenarioId, RemoteProducer remoteProducer) {
        LocalConsumer consumer = consumerCollection.getById(remoteProducer.getConsumerId());
        switch (consumer.getBlockType()) {
            case ACTIVITY:
            case WORKFLOW:
            case CONDITION:
                return startAsync(scenarioId, remoteProducer, consumer);
            case SOURCE:
                return startSync(scenarioId, remoteProducer, consumer);

            default:
                throw new IllegalArgumentException("Unknown init operation type " + consumer.getBlockType());
        }
    }

    public void startFromUser(UUID scenarioId, String workflowName, String workflowVersion, Map<String, Object> params) throws RepeatException {
        LocalConsumer consumer = consumerCollection.findByNameAndVersion(workflowName, workflowVersion);
        if (consumer.getBlockType() != BlockType.WORKFLOW) {
            throw new IllegalStateException("Can't run non-WORKFLOWS from USER");
        }
        Collection<ValueModel> inputValues = consumer.getInputVariables();
        ValueContainer filledValues = ValueContainerFactory.getSimpleContainerByModels(List.of());
        for (ValueModel valueModel: inputValues ){
            filledValues.addValue(valueModel.createFillModel(params.get(valueModel.getName())));
        }

        Identifier producerId = new Identifier(-1L, "USER");
        RemoteProducer remoteProducer = new RemoteProducer(producerId, "USER", filledValues, consumer.getId());
        startWithoutAnswer(scenarioId, remoteProducer, consumer);
    }


    private void startWithoutAnswer(UUID scenarioId, RemoteProducer remoteProducer, LocalConsumer consumer) throws RepeatException {
        Optional<Context> context = contextService.createBlockContext(consumer.getId(), scenarioId, remoteProducer, consumer.getBlockType());

        if (context.isPresent()) {
            userOperationService.executeState(context.get());
        } else {
            throw new RepeatException("OperationRepeat");
        }
    }

    private MessageResponse startAsync(UUID scenarioId, RemoteProducer remoteProducer, LocalConsumer consumer) {
        //выгружает контекст из базы и запускает обработку
        Optional<Context> context = contextService.createBlockContext(consumer.getId(), scenarioId,
                remoteProducer, consumer.getBlockType());

        if (context.isPresent()) {
            asyncOperationService.executeState(context.get());
            return new MessageResponse(HttpStatus.OK, MessageResponse.OK, new ArrayList<>());
        } else {
            Context prevContext = contextService.loadContext(consumer.getId(), scenarioId);
            return new MessageResponse(HttpStatus.OK, MessageResponse.REPEAT, prevContext.getContextOutValues().getValues());
        }
    }


    private MessageResponse startSync(UUID scenarioId, RemoteProducer remoteProducer, LocalConsumer consumer) {
        Context context = null;
        switch (consumer.getBlockType()) {
            case SOURCE:
                context = contextService.createSourceContext(scenarioId, remoteProducer, consumer.getId());
                break;
            case CONDITION:
                context = contextService.createConditionContext(scenarioId, remoteProducer, consumer.getId());
                break;
            default:
                throw new IllegalStateException("Unexpected sync consumer type " + consumer.getBlockType());
        }
        syncOperationService.executeState(context);
        return new MessageResponse(HttpStatus.OK, MessageResponse.OK, context.getContextOutValues().getValues());
    }

//    /**
//     * Запуск воркфлоу от пользователя
//     *
//     * @param workflowName - имя запускаемого воркфлоу
//     * @param version      - версия запускаемого воркфлоу
//     * @param params       - переменные вокрфлоу по именам
//     * @return айди сценария
//     */
//    public UUID startWorkflowByUser(String workflowName, String version, Map<String, Object> params) {
//        UUID scenarioId = UUID.randomUUID();
//        startWorkflowByUser(workflowName, version, params, scenarioId);
//        return scenarioId;
//    }

//    /**
//     * Запуск воркфлоу от пользователя
//     *
//     * @param workflowName - имя запускаемого воркфлоу
//     * @param version      - версия запускаемого воркфлоу
//     * @param params       - переменные вокрфлоу по именам
//     * @param scenarioID   - айди сценария
//     */
//    public void startWorkflowByUser(String workflowName, String version, Map<String, Object> params, UUID scenarioID) {
//
//        //ищем исполняемый воркфлоу
//        LocalActivity executingConsumer = consumerCollection.findByNameAndVersion(workflowName, version);
//
//        log.info("Workflow starting by User: {}, id={}", workflowName, scenarioID);
//        Context context = contextService.fillDefaultContext(executingConsumer.getIdentifier(), scenarioID);
//
//
//        //парсим входящие переменные в переменные воркфлоу
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, ValueModel> consumerValues = executingConsumer.getInputVariables().stream()
//                .collect(Collectors.toMap(ValueModel::getName, Function.identity()));
//        Collection<ValueDto> values = new ArrayList<>(params.size());
//        params.forEach((s, o) -> {
//            try {
//                ValueModel thisValue = consumerValues.get(s);
//                values.add(new ValueDto(thisValue.getId(), s, thisValue.getMultiplicityEnm(), mapper.writeValueAsString(o)));
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        RemoteProducer remoteProducer = new RemoteProducer(
//                new Identifier(-1L, "NONE"),
//                "USER",
//                ValueContainerFactory.getSimpleContainer(values),
//                executingConsumer.getIdentifier()
//        );
//
//        executingConsumer.start(context, remoteProducer, userOperationService);
//    }

//    public MessageResponse startContext(UUID scenarioId, RemoteProducer remoteProducer) {
//        //выгружает контекст из базы и запускает обработку
//        LocalActivity consumer = consumerCollection.getById(remoteProducer.getConsumerId());
//        Context context = contextService.fillDefaultContext(consumer.getIdentifier(), scenarioId);
//        boolean isStarted = executeConsumer(consumer, context, remoteProducer);
//        if (isStarted){
//            return new MessageResponse(HttpStatus.OK, MessageResponse.OK, new ArrayList<>());
//        }else {
//            return new MessageResponse(HttpStatus.OK, MessageResponse.REPEAT, context.getContextOutValues().getModels());
//        }
//
//    }

//    public boolean startContext(ContextEntity entity) {
//        LocalActivity consumer = consumerCollection.getById(entity.getConsumerId());
//        RemoteProducer producer = entity.getProducer();
//        Context context = contextService.fillDefaultContext(entity);
//        return executeConsumer(consumer, context, producer);
//    }

//    private boolean executeConsumer(LocalActivity consumer, Context context, RemoteProducer remoteProducer) {
//        switch (consumer.getBlockType()) {
//            case ACTIVITY:
//                return consumer.start(context, remoteProducer, actionOperationService);
//            case WORKFLOW:
//                return consumer.start(context, remoteProducer, workflowOperationService);
//            default:
//                throw new UnsupportedOperationException("Wrong block type");
//        }
//    }


}
