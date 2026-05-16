package com.petra.lib.actor.local;

import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.operation.OperationService;
import com.petra.lib.utils.id.Identifier;

import java.util.Collection;

public interface LocalConsumer {

    Identifier getId();

    String getName();

    void execute(Context blockContext, OperationService operationService);

    BlockType getBlockType();
    Collection<ValueModel> getInputVariables();


}
