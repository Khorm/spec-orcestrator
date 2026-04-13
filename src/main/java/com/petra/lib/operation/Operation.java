package com.petra.lib.operation;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;

public interface Operation {
    void execute(Context blockContext, OperationService operationService);

    ContextState getState();
}
