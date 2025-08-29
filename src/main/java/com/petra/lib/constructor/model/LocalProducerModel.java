package com.petra.lib.constructor.model;

import java.util.Collection;

public class LocalProducerModel {
    private Long id;
    private String version;

    private String name;
    private Collection<RemoteConsumerModel> consumers;
    private Collection<ValueLoaderModel> lastWorkflowBlockValueParser;
    private Integer lastWorkflowBlockValuesCount;

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

    public Integer getLastWorkflowBlockValuesCount() {
        return lastWorkflowBlockValuesCount;
    }

    public Collection<ValueLoaderModel> getLastWorkflowBlockValueParser() {
        return lastWorkflowBlockValueParser;
    }
}
