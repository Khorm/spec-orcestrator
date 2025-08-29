package com.petra.lib.context.model;

import com.petra.lib.context.source.SourceUserHandler;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.model.ValueModel;

import java.util.Collection;

public class LocalSource {

    private final Identifier id;
    private final String name;
    private final Collection<ValueModel> outputModels;
    private final SourceUserHandler sourceUserHandler;

    public LocalSource(Identifier id, String name, Collection<ValueModel> outputModels, SourceUserHandler sourceUserHandler) {
        this.id = id;
        this.name = name;
        this.outputModels = outputModels;
        this.sourceUserHandler = sourceUserHandler;
    }

    public ValueContainer getOutputEmptyContainer() {
        return ValueContainerFactory.getSimpleContainer(outputModels);
    }

    public SourceUserHandler getSourceUserHandler() {
        return sourceUserHandler;
    }

    public Identifier getIdentifier() {
        return id;
    }
}
