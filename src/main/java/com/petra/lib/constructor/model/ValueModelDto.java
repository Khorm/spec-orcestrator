package com.petra.lib.constructor.model;

import com.petra.lib.constructor.enums.LoaderType;
import com.petra.lib.variable.enums.Multiplicity;

import java.util.Collection;
import java.util.List;

public class ValueModelDto {
    private Long id;
    private String name;
    private String multiplicity;
    private List<Long> parents;
    private String loaderType;
    private Collection<ValueModelDto> children;
    private Long inputValueId;
    private String extractionString;
    private String script;

    private Long sourceId;
    private String sourceVersion;
    private String sourceName;
    private Collection<SourceInputVariableModel> sourceInputVariableModels;


    public LoaderType getLoaderType() {
        return LoaderType.valueOf(loaderType);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Multiplicity getMultiplicity() {
        return Multiplicity.valueOf(multiplicity);
    }

    public List<Long> getParents() {
        return parents;
    }

    public Collection<ValueModelDto> getChildren() {
        return children;
    }

    public Long getInputValueId() {
        return inputValueId;
    }

    public String getExtractionString() {
        return extractionString;
    }

    public String getScript() {
        return script;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Collection<SourceInputVariableModel> getSourceInputVariableModels() {
        return sourceInputVariableModels;
    }
}
