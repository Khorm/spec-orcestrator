package com.petra.lib.context.model;

import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.block.operations.executor.handler.UserActionHandler;
import com.petra.lib.variable.context.ValueContextModel;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.model.ValueModel;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocalConsumer {
    private final ModelIdentifier identifier;
    private final BlockType blockType;
    private final String name;
    private final ValueContextModel startedLoaders;
    private final UserActionHandler userActionHandler;
    private final Map<String, ValueModel> contextOuterValues;

    public LocalConsumer(ModelIdentifier identifier, BlockType blockType, String name,
                         ValueContextModel startedLoaders,
                         UserActionHandler userActionHandler, Collection<ValueModel> contextOuterValues) {
        this.identifier = identifier;
        this.blockType = blockType;
        this.name = name;
        this.startedLoaders = startedLoaders;
        this.userActionHandler = userActionHandler;
        this.contextOuterValues = contextOuterValues.stream().collect(Collectors.toMap(ValueModel::getName, Function.identity()));

    }

    public ModelIdentifier getIdentifier() {
        return identifier;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public String getName() {
        return name;
    }

    public Collection<ValueLoader> getStartedLoaders() {
        return startedLoaders.getStarterLoaders();
    }

    public int getValuesCount() {
        return startedLoaders.getValuesCount();
    }

    public UserActionHandler getUserActionHandler() {
        return userActionHandler;
    }

    public Map<String, ValueModel> getContextOuterValues() {
        return contextOuterValues;
    }
}
