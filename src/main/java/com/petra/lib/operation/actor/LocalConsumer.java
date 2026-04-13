package com.petra.lib.operation.actor;

import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.utils.id.Identifier;

import java.util.List;

public class LocalConsumer {
    private final Identifier identifier;
    private final BlockType blockType;
    private final String name;

    private final List<ValueModel> inputVariables;
    private final List<ValueModel> outputVariables;


    LocalConsumer(Identifier identifier, BlockType blockType, String name,
                  List<ValueModel> inputVariables, List<ValueModel> outputVariables) {
        this.identifier = identifier;
        this.blockType = blockType;
        this.name = name;

        this.inputVariables = inputVariables;
        this.outputVariables = outputVariables;
    }

    public Identifier getIdentifier() {
        return identifier;
    }


    public BlockType getBlockType() {
        return blockType;
    }

    public String getName() {
        return name;
    }

    public List<ValueModel> getInputVariables() {
        return inputVariables;
    }

    public List<ValueModel> getOutputVariables() {
        return outputVariables;
    }
}
