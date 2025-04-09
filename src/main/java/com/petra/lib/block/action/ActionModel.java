package com.petra.lib.block.action;

import com.petra.lib.variable.model.VariableModel;

public class ActionModel {
    private Long id;
    private String version;
    private VariableModel variableModel;

    public Long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public VariableModel getVariableModel() {
        return variableModel;
    }
}
