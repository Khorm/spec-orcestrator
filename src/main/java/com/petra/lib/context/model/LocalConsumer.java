package com.petra.lib.context.model;

import com.petra.lib.context.enums.BlockType;

public class LocalConsumer {
    private final ConsumerIdentifier identifier;
    private final BlockType blockType;
    private final String name;

    //TODO контекст должен изначально заполнятся всеми переменными, просто незаполненные должны быть пустыми
//    private final Collection<ValueModel> contextValues;

    LocalConsumer(ConsumerIdentifier identifier, BlockType blockType, String name) {
        this.identifier = identifier;
        this.blockType = blockType;
        this.name = name;

    }

    public ConsumerIdentifier getIdentifier() {
        return identifier;
    }

    public Identifier getConsumerId(){
        return identifier.getConsumerId();
    }

    public Identifier getWorkflowId(){
        return identifier.getWorkflowId();
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public String getName() {
        return name;
    }




//    public Map<String, ValueModel> getContextOuterValues() {
//        return contextOuterValues;
//    }
}
