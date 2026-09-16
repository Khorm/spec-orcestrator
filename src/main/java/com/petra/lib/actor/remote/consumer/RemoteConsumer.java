package com.petra.lib.actor.remote.consumer;

import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;

import java.util.UUID;

public interface RemoteConsumer {
    void handleAnswer(LinkedConsumerListMessage message);
    void execute(LinkedConsumerListMessage message);
}
