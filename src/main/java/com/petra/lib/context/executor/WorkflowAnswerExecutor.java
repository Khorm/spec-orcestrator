package com.petra.lib.context.executor;

import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.workflow.repo.WorkflowContextRepo;
import com.petra.lib.operation.OperationService;
import com.petra.lib.operation.actor.LocalProducer;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkflowAnswerExecutor {
    Map<Identifier, LocalProducer> producerMap;
    ContextService contextService;
    OperationService blockOperationService;
    OperationService userOperationService;


    public WorkflowAnswerExecutor(Collection<LocalProducer> producers, OperationService blockOperationService,
                                  ContextService contextService,
                                  OperationService userOperationService) {
        this.producerMap = producers.stream().collect(Collectors.toMap(LocalProducer::getWorkflowId, Function.identity()));
        this.contextService = contextService;
        this.blockOperationService = blockOperationService;
        this.userOperationService = userOperationService;
    }

    public void handleAnswerFromBlock(ValueContainer outputValues, Identifier answeredBlockId,
                                      UUID scenarioId, Identifier workflowId, ExecutionStatus executionStatus) {
        Context context = contextService.createContext(workflowId, scenarioId);
        context.load();
        LocalProducer localProducer = producerMap.get(workflowId);

        if (context.getProducer().getServiceName().equals("USER")) {
            localProducer.answerFromBlock(outputValues, answeredBlockId, scenarioId, executionStatus, userOperationService);
        } else {
            localProducer.answerFromBlock(outputValues, answeredBlockId, scenarioId, executionStatus, blockOperationService);
        }

    }


}
