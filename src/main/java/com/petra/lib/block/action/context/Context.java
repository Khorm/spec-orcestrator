package com.petra.lib.block.action.context;

import com.petra.lib.block.model.Identifier;

import java.util.UUID;

public interface Context {
    Identifier getId();
    UUID getScenarioId();
}
