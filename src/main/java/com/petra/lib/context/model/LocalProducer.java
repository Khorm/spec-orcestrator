package com.petra.lib.context.model;

import com.petra.lib.context.Context;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextManager;

import java.util.UUID;

public class LocalProducer {
    private final Identifier workflowId;
    private final RemoteConsumer firstConsumer;
    private final ContextRepo contextRepo;
    private final ValueContextManager valueContextManager;


    LocalProducer(Identifier workflowId,
                  RemoteConsumer firstConsumer, ContextRepo contextRepo,
                  ValueContextManager valueContextManager) {
        this.workflowId = workflowId;
        this.firstConsumer = firstConsumer;
        this.contextRepo = contextRepo;
        this.valueContextManager = valueContextManager;
    }

    public void start(Context context) {
        firstConsumer.execute(context.getContextValues(), context);
    }


    public void answerFromBlock(ValueContainer answerContainer, Identifier answerBlockId, Context context) {
        RemoteConsumer workConsumer = firstConsumer;
        ConsumerIdentifier consumerIdentifier = new ConsumerIdentifier(answerBlockId, workflowId);
        do {
            if (workConsumer.getId().equals(consumerIdentifier)) {
                workConsumer.answer(context.getScenarioId(), answerContainer);
                workConsumer.next().execute(answerContainer, context);
                return;
            }
            workConsumer = workConsumer.next();
        } while (workConsumer.hasNext());

        //вызывать парсинг из последнего блока в выходные переменные воркфлоу
        valueContextManager.start(answerContainer, context.getScenarioId(), new VariableCallback() {
            @Override
            public void loaded(ValueContainer loadedValues) {
                context.setOutValues(loadedValues);
                context.save();
//                operationService.executeState(contextRepo.findContext(scenarioId, workflowId).get());

            }

            @Override
            public void error(Exception e) {
//                Context context = contextRepo.findContext(scenarioId, workflowId).get();
//                context.saveError(e);
//                operationService.executeState(context);
                context.saveError(e);
            }
        });


    }

    public Identifier getWorkflowId() {
        return workflowId;
    }
}
