package com.petra.lib.constructor.model;

import java.util.Collection;

public class LocalConsumerModel {
    private Long id;
    private String version;
    private String blockType;
    private String name;

    private Collection<ValueDto> inputModels;
    private Collection<ValueDto> outputModels;


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

    public Collection<ValueDto> getInputModels() {
        return inputModels;
    }

    public Collection<ValueDto> getOutputModels() {
        return outputModels;
    }
}
