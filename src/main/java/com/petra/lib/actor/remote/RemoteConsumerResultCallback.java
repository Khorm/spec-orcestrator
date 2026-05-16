package com.petra.lib.actor.remote;

import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.operation.OperationService;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;

import java.util.UUID;

@FunctionalInterface
public interface RemoteConsumerResultCallback {
    void callback(ValueContainer answerContainer, Identifier answerBlockId, UUID scenarioId,
                  ExecutionStatus execResult, OperationService operationServic);
}
