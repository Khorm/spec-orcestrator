package com.petra.lib.actor.activity;

import com.petra.lib.actor.remote.consumer.AbsRemoteConsumer;
import com.petra.lib.actor.remote.consumer.LinkedConsumerListMessage;
import com.petra.lib.actor.remote.consumer.RemoteConsumer;
import com.petra.lib.context.ContextService;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.remote.Sender;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.context.ValueContextManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RemoteActivity extends AbsRemoteConsumer {

    RemoteConsumer nextConsumer;
    TransactionManager transactionManager;
    ContextService contextService;

    public RemoteActivity(String activityName, ValueContextManager activityContextManager, String currentServiceName,
                           Sender sender, ConsumerIdentifier id, String
                                  consumerServiceName, RemoteConsumer nextConsumer, TransactionManager transactionManager, ContextService contextService) {
        super(activityName, activityContextManager, currentServiceName,  sender, id, consumerServiceName, contextService);
        this.nextConsumer = nextConsumer;
        this.transactionManager = transactionManager;
        this.contextService = contextService;
    }

    @Override
    protected RemoteConsumer getNextConsumer(LinkedConsumerListMessage message) {
        return nextConsumer;
    }

    /**
     * обновляет контекст воркфлоу новыми значениями из выполненного блока
     *
     * @param answerContainer
     * @param scenarioId
     * @return
     */
    public void updateWorkflowContextValues(Identifier workflowId, UUID scenarioId, ValueContainer answerContainer) {

        try (Transaction tx = transactionManager.createNewTransaction(false, null)) {
            WorkflowContext workflowContext = contextService.loadWorkflowContext(workflowId, scenarioId);

            boolean loaded = workflowContext.lockAndLoad(tx);
            if (!loaded) {
                tx.rollback();
                throw new IllegalStateException("Context not found");
            }

            ValueContainer contextContainer = workflowContext.getContextValues();
            for (ValueDto value : answerContainer.getValues()) {
                contextContainer.setValueJson(value.getId(), value.getJsonValue());
            }
            workflowContext.setContextValues(contextContainer);
            workflowContext.save(tx);
//            return workflowContext.getContextValues();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
