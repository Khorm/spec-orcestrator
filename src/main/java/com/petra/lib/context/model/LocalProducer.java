package com.petra.lib.context.model;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.operation.OperationService;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextModel;

import java.util.UUID;

public class LocalProducer {
    private final Identifier workflowId;
    private final RemoteConsumer firstConsumer;
    private final OperationService operationService;
    private final ContextRepo contextRepo;


    public LocalProducer(Identifier workflowId,
                         RemoteConsumer firstConsumer, OperationService operationService, ContextRepo contextRepo) {
        this.workflowId = workflowId;
        this.firstConsumer = firstConsumer;
        this.operationService = operationService;
        this.contextRepo = contextRepo;
    }

    public void start(Context context) {
        firstConsumer.execute(context.getContextValues(), context.getScenarioId());
    }


    public void answerFromBlock(ValueContainer answerContainer, Identifier answerBlockId, UUID scenarioId) {
        RemoteConsumer workConsumer = firstConsumer;
        ConsumerIdentifier consumerIdentifier = new ConsumerIdentifier(answerBlockId, workflowId);
        do {
            if (workConsumer.getId().equals(consumerIdentifier)) {
                workConsumer.answer(scenarioId, answerContainer);
                workConsumer.next().execute(answerContainer, scenarioId);
                return;
            }
            workConsumer = workConsumer.next();
        } while (workConsumer.hasNext());
        operationService.executeState(contextRepo.findContext(scenarioId, workflowId).get());
    }
}
