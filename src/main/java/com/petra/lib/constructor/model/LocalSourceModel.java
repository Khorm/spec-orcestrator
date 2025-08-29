package com.petra.lib.constructor.model;

import java.util.Collection;

public class LocalSourceModel {
    private Long id;

    private String version;
    private String name;
    private Collection<ValueDto> outputModels;

    public Long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public Collection<ValueDto> getOutputModels() {
        return outputModels;
    }
}
