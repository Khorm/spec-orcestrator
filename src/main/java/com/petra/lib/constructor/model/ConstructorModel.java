package com.petra.lib.constructor.model;

import java.util.Collection;

public class ConstructorModel {
    private Collection<LocalConsumerModel> consumers;
    private Collection<LocalSourceModel> sources;
    private Collection<LocalProducerModel> producers;

    public Collection<LocalConsumerModel> getConsumers() {
        return consumers;
    }

    public Collection<LocalSourceModel> getSources() {
        return sources;
    }

    public Collection<LocalProducerModel> getProducers() {
        return producers;
    }
}
