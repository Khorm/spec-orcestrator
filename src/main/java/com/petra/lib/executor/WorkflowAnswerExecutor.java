package com.petra.lib.executor;

import com.petra.lib.context.ContextService;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.operation.OperationService;
import com.petra.lib.actor.LocalProducer;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class WorkflowAnswerExecutor {
    Map<Identifier, LocalProducer> producerMap;
    ContextService contextService;
    OperationService blockOperationService;
    OperationService userOperationService;
    TransactionManager transactionManager;


    public WorkflowAnswerExecutor(Collection<LocalProducer> producers, OperationService blockOperationService,
                                  ContextService contextService,
                                  OperationService userOperationService, TransactionManager transactionManager) {
        this.producerMap = producers.stream().collect(Collectors.toMap(LocalProducer::getWorkflowId, Function.identity()));
        this.contextService = contextService;
        this.blockOperationService = blockOperationService;
        this.userOperationService = userOperationService;
        this.transactionManager = transactionManager;
    }

    public void handleAnswerFromBlock(ValueContainer outputValues, Identifier answeredBlockId,
                                      UUID scenarioId, Identifier workflowId, ExecutionStatus executionStatus) {
        try {


            Context context = contextService.createContext(workflowId, scenarioId);
            try (Transaction transaction = transactionManager.createNewTransaction(true, null)) {
                context.load(transaction);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            LocalProducer localProducer = producerMap.get(workflowId);
            log.debug("{} answer to {} ", scenarioId, localProducer.getWorkflowName());
            if (context.getProducer().getServiceName().equals("USER")) {
                localProducer.answerFromBlock(outputValues, answeredBlockId, scenarioId, executionStatus, userOperationService);
            } else {
                localProducer.answerFromBlock(outputValues, answeredBlockId, scenarioId, executionStatus, blockOperationService);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("Error in handling Answer {}", e);
            throw e;
        }

    }


}
