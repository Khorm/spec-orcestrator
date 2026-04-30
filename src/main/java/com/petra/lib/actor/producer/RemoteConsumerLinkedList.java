package com.petra.lib.actor.producer;

import com.petra.lib.utils.id.ConsumerIdentifier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RemoteConsumerLinkedList {
    @Getter
    RemoteConsumer firstConsumer;

    Optional<RemoteConsumer> findConsumer(ConsumerIdentifier consumerId){
        RemoteConsumer consumer = firstConsumer;
        while (consumer != null){
            if (consumer.getId().equals(consumerId)){
                return Optional.of(consumer);
            }

            if (consumer.hasNext()){
                consumer = consumer.next();
            }else {
                consumer = null;
            }
        }
        return Optional.empty();
    }


}
