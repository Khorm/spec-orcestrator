package com.petra.lib.context.block;

import com.petra.lib.context.ContextState;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueModel;
import com.petra.lib.variable.value.Value;

import java.util.List;
import java.util.UUID;

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

    private final BlockType blockType;

    /**
     * Список исходящих переменных контекста
     */
    private final ValueContainer outContextValues;

    /**
     * Стейт в котором находтся котекст
     */
    private ContextState state;

    /**
     * Результат выполнения
     */
    private ExecutionStatus executionStatus = ExecutionStatus.OK;


    public ContextEntity(UUID scenarioId,
                         Identifier consumerId,
                         String producerServiceName,
                         Identifier producerBlockId,
                         ContextState contextState,
                         ExecutionStatus executionStatus,
                         List<ValueModel> producerValues, BlockType blockType, List<ValueModel> outValues
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
                         ContextState state, ValueContainer inputContextValues, ValueContainer outContextValues) {
        this.scenarioId = scenarioId;
        this.producer = producer;
        this.blockType = blockType;
        this.inputContextValues = inputContextValues;
        this.state = state;
        this.outContextValues = outContextValues;
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public RemoteProducer getProducer() {
        return producer;
    }

    public Identifier getConsumerId() {
        return producer.getConsumerId();
    }

    public ValueContainer getInputContextValues() {
        return inputContextValues;
    }

    public ContextState getState() {
        return state;
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public ValueContainer getOutContextValues() {
        return outContextValues;
    }

    public void setState(ContextState state) {
        this.state = state;
    }


    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }



    public BlockType getBlockType() {
        return blockType;
    }


    public void setOutContextValues(ValueContainer outValues) {
        for(Value value : outValues.getValues()) {
            outContextValues.setValue(value);
        }
    }
}
