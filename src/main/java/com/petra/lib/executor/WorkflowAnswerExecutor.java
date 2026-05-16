package com.petra.lib.executor;

import com.petra.lib.actor.local.producer.LocalProducer;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.operation.OperationService;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Обслаживает принятие сообщений от блоков внутри воркфлоу
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class WorkflowAnswerExecutor {
    ConsumerCollection producerMap;
    ContextService contextService;
    OperationService blockOperationService;
    OperationService userOperationService;


    public WorkflowAnswerExecutor(ConsumerCollection producerMap, OperationService blockOperationService,
                                  ContextService contextService,
                                  OperationService userOperationService) {
        this.producerMap = producerMap;
        this.contextService = contextService;
        this.blockOperationService = blockOperationService;
        this.userOperationService = userOperationService;
    }

    public void handleAnswerFromBlock(ValueContainer outputValues, Identifier answeredBlockId,
                                      UUID scenarioId, Identifier workflowId, ExecutionStatus executionStatus) {
        try {
            Context context = contextService.loadContext(workflowId, scenarioId);

            LocalProducer localProducer = producerMap.getProducer(workflowId);
            log.debug("{} answer to {} ", scenarioId, localProducer.getName());
            if (context.getProducer().getServiceName().equals("USER")) {
                localProducer.answerFromBlock(outputValues, answeredBlockId, scenarioId, executionStatus, userOperationService);
            } else {
                localProducer.answerFromBlock(outputValues, answeredBlockId, scenarioId, executionStatus, blockOperationService);
            }
        } catch (Exception e) {
            log.error("Error in handling Answer {}", e);
            throw e;
        }

    }


}
