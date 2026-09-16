package com.petra.lib.actor.remote.consumer;

import com.petra.lib.constructor.model.RemoteConsumerModel;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.remote.Sender;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.context.ValueContextManager;

import java.util.Map;
@Deprecated
public class RemoteConsumerImplCondition /* extends RemoteConsumerImpl*/ {

//    Map<Integer, RemoteConsumerImpl> nextConsumers;
//
//    public RemoteConsumerImplCondition(RemoteConsumerModel remoteConsumerModel, ValueContextManager valueContextManager, String currentServiceName, RemoteConsumerImpl nextConsumer, Sender sender, BlockType blockType) {
//        super(remoteConsumerModel, valueContextManager, currentServiceName, nextConsumer, sender, blockType);
//    }
//
//
//    @Override
//    public boolean hasNext() {
//        return !nextConsumers.isEmpty();
//    }
//
//    /**
//     * В результ записывается номер следующего вызываемого пина
//     * @param answerContainer
//     * @return
//     */
//    public RemoteConsumerImpl nextByConsumer(ValueContainer answerContainer) {
//
//        return nextConsumers.get(answerContainer.getValue("result"));
//    }
}
