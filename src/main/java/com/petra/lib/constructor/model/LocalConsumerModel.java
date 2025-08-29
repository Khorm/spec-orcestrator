package com.petra.lib.constructor.model;

import java.util.Collection;

public class LocalConsumerModel {
    private Long id;
    private String version;

    private String blockType;
    private String name;

    private Collection<ValueLoaderModel> valueLoaders;
    private Integer valuesCount;
    private Collection<ValueDto> outerValues;

    public Long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getBlockType() {
        return blockType;
    }

    public String getName() {
        return name;
    }

    public Collection<ValueLoaderModel> getValueLoaders() {
        return valueLoaders;
    }

    public Integer getValuesCount() {
        return valuesCount;
    }

    public Collection<ValueDto> getOuterValues() {
        return outerValues;
    }
}
