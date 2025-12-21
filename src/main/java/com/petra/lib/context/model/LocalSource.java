package com.petra.lib.context.model;

import com.petra.lib.constructor.model.ValueDto;
import com.petra.lib.context.source.SourceUserHandler;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LocalSource {

    private final Identifier id;
    private final String name;
    private final ValueDto outputModels;
    private final SourceUserHandler sourceUserHandler;

    public LocalSource(Identifier id, String name, ValueDto outputModels, SourceUserHandler sourceUserHandler) {
        this.id = id;
        this.name = name;
        this.outputModels = outputModels;
        this.sourceUserHandler = sourceUserHandler;
    }

    public ValueContainer getOutputEmptyContainer() {
        return ValueContainerFactory.getSimpleContainer(Collections.singletonList(outputModels));
    }

    public SourceUserHandler getSourceUserHandler() {
        return sourceUserHandler;
    }

    public Identifier getIdentifier() {
        return id;
    }
}
