package com.petra.lib.constructor.model;

import java.util.Collection;
import java.util.List;

public class LocalConsumerModel {
    private Long id;
    private String version;
    private String blockType;
    private String name;

    private List<ValueDto> inputModels;
    private List<ValueDto> outputModels;


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

    public List<ValueDto> getInputModels() {
        return inputModels;
    }

    public List<ValueDto> getOutputModels() {
        return outputModels;
    }
}
