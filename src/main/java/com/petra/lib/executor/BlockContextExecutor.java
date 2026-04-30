package com.petra.lib.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.actor.LocalConsumer;
import com.petra.lib.actor.RemoteProducer;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.operation.OperationService;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BlockContextExecutor {
    OperationService workflowOperationService;
    ConsumerCollection consumerCollection;
    OperationService actionOperationService;
    ContextService contextService;
    OperationService userOperationService;




    /**
     * Запуск воркфлоу от пользователя
     *
     * @param workflowName - имя запускаемого воркфлоу
     * @param version      - версия запускаемого воркфлоу
     * @param params       - переменные вокрфлоу по именам
     * @return айди сценария
     */
    public UUID startWorkflowByUser(String workflowName, String version, Map<String, Object> params) {
        UUID scenarioId = UUID.randomUUID();
        startWorkflowByUser(workflowName, version, params, scenarioId);
        return scenarioId;
    }

    /**
     * Запуск воркфлоу от пользователя
     *
     * @param workflowName - имя запускаемого воркфлоу
     * @param version      - версия запускаемого воркфлоу
     * @param params       - переменные вокрфлоу по именам
     * @param scenarioID   - айди сценария
     */
    public void startWorkflowByUser(String workflowName, String version, Map<String, Object> params, UUID scenarioID) {

        //ищем исполняемый воркфлоу
        LocalConsumer executingConsumer = consumerCollection.findByNameAndVersion(workflowName, version);

        log.info("Workflow starting by User: {}, id={}", workflowName, scenarioID);
        Context context = contextService.fillDefaultContext(executingConsumer.getIdentifier(), scenarioID);


        //парсим входящие переменные в переменные воркфлоу
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

        executingConsumer.start(context, remoteProducer, userOperationService);
    }

    public MessageResponse startContext(UUID scenarioId, RemoteProducer remoteProducer) {
        //выгружает контекст из базы и запускает обработку
        LocalConsumer consumer = consumerCollection.getById(remoteProducer.getConsumerId());
        Context context = contextService.fillDefaultContext(consumer.getIdentifier(), scenarioId);
        boolean isStarted = executeConsumer(consumer, context, remoteProducer);
        if (isStarted){
            return new MessageResponse(HttpStatus.OK, MessageResponse.OK, new ArrayList<>());
        }else {
            return new MessageResponse(HttpStatus.OK, MessageResponse.REPEAT, context.getContextOutValues().getModels());
        }

    }

    public boolean startContext(ContextEntity entity) {
        LocalConsumer consumer = consumerCollection.getById(entity.getConsumerId());
        RemoteProducer producer = entity.getProducer();
        Context context = contextService.fillDefaultContext(entity);
        return executeConsumer(consumer, context, producer);
    }

    private boolean executeConsumer(LocalConsumer consumer, Context context, RemoteProducer remoteProducer) {
        switch (consumer.getBlockType()) {
            case ACTION:
                return consumer.start(context, remoteProducer, actionOperationService);
            case WORKFLOW:
                return consumer.start(context, remoteProducer, workflowOperationService);
            default:
                throw new UnsupportedOperationException("Wrong block type");
        }
    }


}
