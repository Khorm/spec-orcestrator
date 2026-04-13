package com.petra.lib.operation.actor;

import com.petra.lib.operation.OperationService;

import java.util.UUID;

@FunctionalInterface
public interface RemoteConsumerResultCallback {
    void callback(UUID scenarioId, RemoteConsumer consumer, OperationService operationService);
}
