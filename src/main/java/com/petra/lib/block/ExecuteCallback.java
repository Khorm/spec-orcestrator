package com.petra.lib.block;

import com.petra.lib.block.action.context.ActionContext;
import com.petra.lib.block.enums.BlockManager;

import java.util.UUID;

public interface ExecuteCallback {
    void executeNext(ActionContext actionContext, BlockManager executedManager);
    void executeNext(UUID scenarioId, BlockManager executedManager);
    void error(Exception e, UUID scenarioId);
    void error(Exception e, ActionContext actionContext);
}
