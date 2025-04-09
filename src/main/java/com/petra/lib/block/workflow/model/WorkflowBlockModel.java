package com.petra.lib.block.workflow.model;

import com.petra.lib.variable.model.VariableModel;

public class WorkflowBlockModel {
    private Long id;
    private String version;
    private String name;
    private IdModel nextBlock;
    private  String consumerServiceURL;
    private VariableModel variableModel;

    public Long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public IdModel getNextBlock() {
        return nextBlock;
    }

    public String getConsumerServiceURL() {
        return consumerServiceURL;
    }

    public VariableModel getVariableModel() {
        return variableModel;
    }
}
