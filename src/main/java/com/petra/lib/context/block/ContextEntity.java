package com.petra.lib.context.block;

import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.operation.actor.RemoteProducer;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.value.Value;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ContextEntity {
    /**
     * АЙДИ бизнеспроцесса
     */
    private final UUID scenarioId;

    private final RemoteProducer producer;

    /**
     * Список входящих и загруженых переменных контекста
     */
    private final ValueContainer inputContextValues;

    @Getter
    private final BlockType blockType;

    /**
     * Список исходящих переменных контекста
     */
    private final ValueContainer outContextValues;
    boolean valuesChanged;

    /**
     * Стейт в котором находтся котекст
     */
    private ContextState state;
    boolean areStateChanged;

    /**
     * Результат выполнения
     */
    private ExecutionStatus executionStatus = ExecutionStatus.OK;
    boolean areExecStatusChanged;



    public ContextEntity(UUID scenarioId,
                         Identifier consumerId,
                         String producerServiceName,
                         Identifier producerBlockId,
                         ContextState contextState,
                         ExecutionStatus executionStatus,
                         List<ValueDto> producerValues, BlockType blockType, List<ValueDto> outValues
    ) {
        this.scenarioId = scenarioId;
        this.state = contextState;
        this.inputContextValues = ValueContainerFactory.getSimpleContainer(producerValues);
        this.blockType = blockType;
        this.outContextValues = ValueContainerFactory.getSimpleContainer(outValues);
        this.producer = new RemoteProducer(producerBlockId, producerServiceName,
                ValueContainerFactory.getSimpleContainer(producerValues), consumerId);
        this.executionStatus = executionStatus;
    }

    public ContextEntity(UUID scenarioId, RemoteProducer producer, BlockType blockType,
                         ContextState state, ValueContainer outContextValues) {
        this.scenarioId = scenarioId;
        this.producer = producer;
        this.blockType = blockType;
        this.inputContextValues = producer.getSendValuesContainer();
        this.state = state;
        this.outContextValues = Optional.ofNullable(outContextValues).orElse(ValueContainerFactory.getSimpleContainer());
    }


    public Identifier getConsumerId() {
        return producer.getConsumerId();
    }


    void setState(ContextState state) {
        this.state = state;
        areStateChanged = true;
    }


    void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
        areExecStatusChanged = true;
    }


    void setOutContextValues(ValueContainer outValues) {
        for(Value value : outValues.getValues()) {
            outContextValues.setValue(value);
        }
        valuesChanged = true;
    }


}
