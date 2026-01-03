package com.petra.lib.context.model;

import com.petra.lib.constructor.model.ValueDto;
import com.petra.lib.context.enums.BlockType;

import java.util.Collection;
import java.util.List;

public class LocalConsumer {
    private final Identifier identifier;
    private final BlockType blockType;
    private final String name;

    private final List<ValueDto> inputVariables;
    private final List<ValueDto> outputVariables;


    LocalConsumer(Identifier identifier, BlockType blockType, String name,
                  List<ValueDto> inputVariables, List<ValueDto> outputVariables) {
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

    public List<ValueDto> getInputVariables() {
        return inputVariables;
    }

    public List<ValueDto> getOutputVariables() {
        return outputVariables;
    }
}
