package com.petra.lib.constructor.model;

import lombok.Getter;

import java.util.Collection;

@Getter
public class LocalProducerModel {
    private Long id;
    private String version;

    private String name;
    private Collection<RemoteConsumerModel> consumers;
    private Collection<ValueModelDto> exitValues;

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


}
