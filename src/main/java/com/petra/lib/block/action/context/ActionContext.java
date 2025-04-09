package com.petra.lib.block.action.context;

import com.petra.lib.block.enums.HistoryType;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueContainer;

import java.util.UUID;

/**
 * Общий контекст исполнения в текущем блоке
 */


public class ActionContext {

    /**
     * АЙДИ бизнеспроцесса
     */
    private final UUID scenarioId;

    /**
     * ID активности
     */
    private final Identifier consumerBlockId;

    private final String producerServiceUrl;

    private final Identifier producerBlockId;

    private final ValueContainer consumerContextValues;


    /**
     * Текущая стадия выполнения активности
     */
//    private final BlockState consumerStatus;

    private final HistoryType historyType;


    public ActionContext(UUID scenarioId, Identifier consumerBlockId, String producerServiceUrl, Identifier producerBlockId,
                         HistoryType historyType, String values) {
        this.scenarioId = scenarioId;
        this.consumerBlockId = consumerBlockId;
        this.producerServiceUrl = producerServiceUrl;
        this.producerBlockId = producerBlockId;
        this.historyType = historyType;
        this.consumerContextValues = new ValueContainer(values);
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public Identifier getConsumerBlockId() {
        return consumerBlockId;
    }


    public String getProducerServiceUrl() {
        return producerServiceUrl;
    }

    public Identifier getProducerBlockId() {
        return producerBlockId;
    }


    public ValueContainer getConsumerContextValues() {
        return consumerContextValues;
    }

//    public void setCurrentValues(ValueContainer currentValues) {
//        this.consumerContextValues = currentValues;
//    }



//    public BlockState getConsumerStatus() {
//        return consumerStatus;
//    }

    public HistoryType getHistoryType() {
        return historyType;
    }

    public String getConsumerValuesJson() {
        return consumerContextValues.toJson();
    }

    public Value getValue(String name) {
        return consumerContextValues.getValue(name);
    }

    public void setValue(Value value) {
        consumerContextValues.setValue(value);
    }


    //    /**
//     * Текущие переменные активности
//     */
//    final ValuesContainer actionVariables;
//
//
//    /**
//     * Исходник, откуда пришел сигнал на инициализацию
//     */
//    final Long requestWorkflowId;
//    final String requestServiceName;
//
//    public synchronized void setActionState(BlockState actionState) {
//        this.actionState = actionState;
//    }
//
//    public BlockState getState() {
//        return actionState;
//    }
//
//    public void setValue(ProcessValue value) {
//        actionVariables.addValue(value);
//    }
//
//    public ProcessValue getValueById(Long variableId) {
//        return actionVariables.getValueById(variableId);
//    }
//
//    public ProcessValue getValueByVariableName(String variableName) {
//        return actionVariables.getValueByName(variableName);
//    }

}
