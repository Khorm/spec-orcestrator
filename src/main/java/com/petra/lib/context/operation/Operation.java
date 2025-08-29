package com.petra.lib.context.operation;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.block.BlockContext;

public interface Operation<T extends Context> {
    void execute(T blockContext);
    ContextState getState();
}
