package com.petra.lib.context.model;

import com.petra.lib.context.enums.BlockType;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.variable.context.ValueContextModel;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.model.ValueModel;

import javax.persistence.Id;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocalConsumer {
    private final ConsumerIdentifier identifier;
    private final BlockType blockType;
    private final String name;
    private final ValueContextModel startedLoaders;

    //TODO контекст должен изначально заполнятся всеми переменными, просто незаполненные должны быть пустыми
    private final Collection<ValueModel> contextValues;

    public LocalConsumer(ConsumerIdentifier identifier, BlockType blockType, String name,
                         ValueContextModel startedLoaders,
                         List<ValueModel> contextValues,
                         List<ValueModel> inputValues) {
        this.identifier = identifier;
        this.blockType = blockType;
        this.name = name;
        this.startedLoaders = startedLoaders;
        this.contextValues = contextValues;
        this.contextValues.addAll(inputValues);

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

    public ValueContextModel getStartedLoaders() {
        return startedLoaders;
    }



//    public Map<String, ValueModel> getContextOuterValues() {
//        return contextOuterValues;
//    }
}
