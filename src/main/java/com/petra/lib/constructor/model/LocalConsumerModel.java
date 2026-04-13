package com.petra.lib.constructor.model;

import java.util.List;

public class LocalConsumerModel {
    private Long id;
    private String version;
    private String blockType;
    private String name;

    private List<ValueModel> inputModels;
    private List<ValueModel> outputModels;


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

    public List<ValueModel> getInputModels() {
        return inputModels;
    }

    public List<ValueModel> getOutputModels() {
        return outputModels;
    }
}
