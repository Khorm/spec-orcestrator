package com.petra.lib.constructor.model;

import java.util.Collection;

public class LocalProducerModel {
    private Long id;
    private String version;

    private String name;
    private Collection<RemoteConsumerModel> consumers;
    private ValuesCollectionModel exitValues;

    public Long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public Collection<RemoteConsumerModel> getConsumers() {
        return consumers;
    }

    public String getName() {
        return name;
    }

    public ValuesCollectionModel getExitValues() {
        return exitValues;
    }
}
