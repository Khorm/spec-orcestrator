package com.petra.lib.actor.remote.consumer;

import com.petra.lib.PetraException;

public class RemoteStart implements RemoteConsumer {

    private final RemoteConsumer nextConsumer;

    public RemoteStart(RemoteConsumer nextConsumer) {
        this.nextConsumer = nextConsumer;
    }

    @Override
    public void handleAnswer(LinkedConsumerListMessage message) {
        throw new PetraException("Not supported for start");
    }

    @Override
    public void execute(LinkedConsumerListMessage message) {
         nextConsumer.execute(message);
    }

}
