package com.petra.lib.actor.remote.consumer;

import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.remote.Sender;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextManager;

import java.util.Map;

public class RemoteConsumerCondition extends RemoteConsumer{

    Map<Integer, RemoteConsumer> nextConsumers;

    public RemoteConsumerCondition(RemoteConsumerModel remoteConsumerModel, ValueContextManager valueContextManager, String currentServiceName, RemoteConsumer nextConsumer, Sender sender, BlockType blockType) {
        super(remoteConsumerModel, valueContextManager, currentServiceName, nextConsumer, sender, blockType);
    }


    @Override
    public boolean hasNext() {
        return !nextConsumers.isEmpty();
    }

    /**
     * В результ записывается номер следующего вызываемого пина
     * @param answerContainer
     * @return
     */
    public RemoteConsumer nextByConsumer(ValueContainer answerContainer) {

        return nextConsumers.get(answerContainer.getValue("result"));
    }
}
