package com.petra.lib.actor.local.source;

import com.petra.lib.actor.local.LocalConsumer;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.operation.OperationService;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainerFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LocalSource implements LocalConsumer {

    Identifier id;
    String name;
    ValueModel outputModel;
    SourceUserHandler sourceUserHandler;
    TransactionManager transactionManager;
    Collection<ValueModel> inputValues;


    public Identifier getIdentifier() {
        return id;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(Context blockContext, OperationService operationService) {

        EntityManager entityManager = transactionManager.getEntityManager();
        SourceUserContext sourceUserContext = new SourceUserContext(blockContext.getContextInputValues(),
                entityManager,
                ValueContainerFactory.getSimpleContainerByModels(Collections.singletonList(outputModel)));
        sourceUserHandler.executeSource(sourceUserContext);

        blockContext.setOutValues(sourceUserContext.getOutputValues());
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.SOURCE;
    }

    @Override
    public Collection<ValueModel> getInputVariables() {
        return inputValues;
    }
}
