package com.petra.lib.variable.model;

import java.util.List;

public class VariableModel {
    private Long variablesCount;
    private List<VariableLoaderModel> variableLoaderModelList;



    public Long getVariablesCount() {
        return variablesCount;
    }

    public List<VariableLoaderModel> getVariables() {
        return variableLoaderModelList;
    }
}
