package com.petra.lib.executor;

import com.petra.lib.actor.LocalConsumer;
import com.petra.lib.utils.id.Identifier;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Список всех локальных консумеров
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConsumerCollection {
    final Map<Identifier, LocalConsumer> consumerMap;

    public ConsumerCollection(Collection<LocalConsumer> consumers) {
        this.consumerMap = consumers.stream().collect(Collectors.toMap(LocalConsumer::getIdentifier, Function.identity()));
    }


    public LocalConsumer findByNameAndVersion(String name, String version){
        LocalConsumer executingConsumer = null;
        for (Map.Entry<Identifier, LocalConsumer> entry : consumerMap.entrySet()) {
            if (entry.getKey().getVersion().equals(version) && entry.getValue().getName().equals(name)) {
                executingConsumer = entry.getValue();
                break;
            }
        }
        if (executingConsumer == null) throw new RuntimeException("No such local consumer");

        return executingConsumer;
    }

    public LocalConsumer getById( Identifier identifier ){
        return consumerMap.get(identifier);
    }
}
