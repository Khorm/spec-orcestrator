package com.petra.lib.context.model;

import com.petra.lib.context.enums.BlockType;

public class LocalConsumer {
    private final Identifier identifier;
    private final BlockType blockType;
    private final String name;

    //TODO контекст должен изначально заполнятся всеми переменными, просто незаполненные должны быть пустыми
//    private final Collection<ValueModel> contextValues;

    LocalConsumer(Identifier identifier, BlockType blockType, String name) {
        this.identifier = identifier;
        this.blockType = blockType;
        this.name = name;

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

}
