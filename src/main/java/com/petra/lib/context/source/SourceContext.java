package com.petra.lib.context.source;

import com.petra.lib.operation.actor.LocalSource;
import com.petra.lib.variable.container.ValueContainer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

class SourceContext {

    private final ValueContainer inputValues;
    private final LocalSource source;
    private final EntityManagerFactory entityManagerFactory;
    SourceContext(ValueContainer inputValues, LocalSource source, EntityManagerFactory entityManagerFactory) {
        this.inputValues = inputValues;
        this.source = source;
        this.entityManagerFactory = entityManagerFactory;
    }

    public ValueContainer execute(){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        SourceUserContext sourceUserContext = new SourceUserContext(inputValues, entityManager, source.getOutputEmptyContainer());
        source.getSourceUserHandler().executeSource(sourceUserContext);
        return sourceUserContext.getOutputValues();
    }
}
