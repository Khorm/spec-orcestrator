package com.petra.lib.operation;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;

public interface Operation {
    void execute(Context blockContext);

    ContextState getState();
}
