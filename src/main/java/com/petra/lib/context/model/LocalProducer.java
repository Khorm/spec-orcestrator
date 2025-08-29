package com.petra.lib.context.model;

import com.petra.lib.variable.context.ValueContextModel;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class LocalProducer {
    private final ModelIdentifier identifier;
    private final List<RemoteConsumer> workflowConsumers;
    private final String name;
    private final ValueContextModel lastWorkflowBlockOuterParser;

    public LocalProducer(ModelIdentifier identifier, List<RemoteConsumer> workflowConsumers,
                         String name, ValueContextModel lastWorkflowBlockOuterParser) {
        this.identifier = identifier;
        this.workflowConsumers = workflowConsumers;
        this.name = name;
        this.lastWorkflowBlockOuterParser = lastWorkflowBlockOuterParser;
    }

    public ModelIdentifier getIdentifier() {
        return identifier;
    }

    public List<RemoteConsumer> getWorkflowConsumers() {
        return workflowConsumers;
    }

    public String getName() {
        return name;
    }

    public Optional<RemoteConsumer> getNextConsumer(RemoteConsumer currentConsumer) {
        Iterator<RemoteConsumer> iter = workflowConsumers.listIterator();
        while (iter.hasNext()) {
            if (iter.next().getIdentifier().equals(currentConsumer.getIdentifier())) {
                return Optional.of(iter.next());
            }
        }
        return Optional.empty();

    }

    public ValueContextModel getLastWorkflowBlockOuterParser() {
        return lastWorkflowBlockOuterParser;
    }
}
