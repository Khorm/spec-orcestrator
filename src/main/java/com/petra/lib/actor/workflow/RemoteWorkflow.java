package com.petra.lib.actor.workflow;

import com.petra.lib.actor.activity.RemoteActivity;
import com.petra.lib.actor.remote.consumer.RemoteConsumer;
import com.petra.lib.context.ContextService;
import com.petra.lib.remote.Sender;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.ConsumerIdentifier;
import com.petra.lib.variable.context.ValueContextManager;

public class RemoteWorkflow extends RemoteActivity {
    public RemoteWorkflow(String activityName, ValueContextManager activityContextManager, String currentServiceName, Sender sender, ConsumerIdentifier id, String consumerServiceName,
                          RemoteConsumer nextConsumer, TransactionManager transactionManager, ContextService contextService) {
        super(activityName, activityContextManager, currentServiceName, sender, id, consumerServiceName, nextConsumer, transactionManager, contextService);
    }
}
