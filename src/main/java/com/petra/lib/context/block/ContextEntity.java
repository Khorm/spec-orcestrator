package com.petra.lib.context.block;

import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;

import java.util.UUID;

public class ContextEntity {
    /**
     * АЙДИ бизнеспроцесса
     */
    private final UUID scenarioId;

    private final RemoteProducer producer;

    private final Identifier consumerId;

    /**
     * Список входящих и загруженых переменных контекста
     */
    private ValueContainer inputContextValues;

    /**
     * Список исходящих переменных контекста
     */
    private ValueContainer outContextValues;

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
                         String values,
                         ContextState contextState,
                         ExecutionStatus executionStatus,
                         String producerValues
    ) {
        this.scenarioId = scenarioId;
        this.state = contextState;
        this.inputContextValues = ValueContainerFactory.getSimpleContainer(values);
        this.consumerId = consumerId;
        this.producer = new RemoteProducer(producerBlockId, producerServiceName,
                ValueContainerFactory.getSimpleContainer(producerValues), consumerId);
        this.executionStatus = executionStatus;
    }

    public ContextEntity(UUID scenarioId, RemoteProducer producer, Identifier consumerId,
                         ContextState state) {
        this.scenarioId = scenarioId;
        this.producer = producer;
        this.consumerId = consumerId;
        this.inputContextValues = ValueContainerFactory.getSimpleContainer();
        this.state = state;
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public RemoteProducer getProducer() {
        return producer;
    }

    public Identifier getConsumerId() {
        return consumerId;
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

    public void setInputContextValues(ValueContainer inputContextValues) {
        this.inputContextValues = inputContextValues;
    }

    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    public void setOutContextValues(ValueContainer outContextValues) {
        this.outContextValues = outContextValues;
    }
}
