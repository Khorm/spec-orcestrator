package com.petra.lib.context.operation;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.block.BlockContext;
@Deprecated
interface OperationService<T extends Context> {
    void executeState(T blockContext, ContextState state);

}
