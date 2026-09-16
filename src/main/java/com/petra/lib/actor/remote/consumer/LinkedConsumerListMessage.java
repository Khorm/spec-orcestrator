package com.petra.lib.actor.remote.consumer;

import com.petra.lib.actor.remote.RemoteConsumerResultCallback;
import com.petra.lib.operation.OperationService;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LinkedConsumerListMessage {

    Identifier answerId;
    ValueContainer answerVariables;
    UUID scenarioId;
    ValueContainer workflowContextVariables;
    RemoteConsumerResultCallback skip;
    OperationService operationService;

    public void addContextVariable(ValueContainer result) {
        for (ValueDto valueDto : result.getValues()) {
            workflowContextVariables.addValue(valueDto);
        }
    }
}
