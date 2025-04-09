package com.petra.lib.variable.model;

import com.petra.lib.variable.value.Multiplicity;

import java.util.List;

public class VariableLoaderModel {
    private Long variableId;
    private List<Long> childVariables;
    private List<Long> parentVariables;

    private LoaderType loaderType;

    private Multiplicity multiplicity;

    private String name;

    private String script;

    private Long sourceId;

    private String sourceVersion;

    private String sourceUrl;

    private Long sourceVariable;

    private VariableModel sourceVariableModel;

    public Long getVariableId() {
        return variableId;
    }

    public List<Long> getChildVariables() {
        return childVariables;
    }

    public List<Long> getParentVariables() {
        return parentVariables;
    }

    public LoaderType getLoaderType() {
        return loaderType;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    public String getName() {
        return name;
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

    public String getSourceUrl() {
        return sourceUrl;
    }

    public Long getSourceVariable() {
        return sourceVariable;
    }

    public VariableModel getSourceVariableModel() {
        return sourceVariableModel;
    }
}
