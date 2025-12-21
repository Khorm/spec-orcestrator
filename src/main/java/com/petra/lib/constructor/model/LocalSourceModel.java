package com.petra.lib.constructor.model;

import java.util.Collection;
import java.util.List;

public class LocalSourceModel {
    private Long id;

    private String version;
    private String name;
    private Collection<ValueDto> inputModels;
    private ValueDto outputModels;

    public Long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public ValueDto getOutputModels() {
        return outputModels;
    }

    public Collection<ValueDto> getInputModels() {
        return inputModels;
    }
}
