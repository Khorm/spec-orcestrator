package com.petra.lib.actor;

import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.context.source.SourceUserHandler;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;

import java.util.Collections;

public class LocalSource {

    private final Identifier id;
    private final String name;
    private final ValueModel outputModels;
    private final SourceUserHandler sourceUserHandler;

    public LocalSource(Identifier id, String name, ValueModel outputModels, SourceUserHandler sourceUserHandler) {
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
