package com.petra.lib.variable.context;

import com.petra.lib.block.dto.SourceResponseDto;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.sender.SenderCallback;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueContainer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ValueContext {
    private final Set<Long> loadedValues;
    private final long valuesCount;
    private final ValueContainer inputValues;
    private final ValueContainer currentValues;
    private final SourceLoadContainer sourceLoadContainer;
    private final VariableCallback variableCallback;
    private final UUID scenarioId;
    private final Identifier currentId;



    public ValueContext(String inputValues, VariableCallback variableCallback, long valuesCount, UUID scenarioId, Identifier currentId) {
        this.valuesCount = valuesCount;
        this.variableCallback = variableCallback;
        this.scenarioId = scenarioId;
        this.currentId = currentId;
        this.loadedValues = new HashSet<>();
        this.inputValues = new ValueContainer(inputValues);
        currentValues = new ValueContainer();
        sourceLoadContainer = new SourceLoadContainer();
    }

    public synchronized Value getInputValue(long signalInputId) {
        return inputValues.getValue(signalInputId);
    }

    public synchronized Value getCurentValue(long signalVariableId) {
        return currentValues.getValue(signalVariableId);
    }

    public synchronized void setValue(Value value) {
        currentValues.setValue(value);
        loadedValues.add(value.getId());
        if (loadedValues.size() == valuesCount){
            variableCallback.loaded(currentValues);
        }
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public synchronized boolean areValuesLoaded(Collection<Long> variableIds) {
        return loadedValues.containsAll(variableIds);
    }

    public synchronized String getJsonValues() {
        return currentValues.toJson();
    }

    public Long getCurrentBlockId() {
        return currentId.getId();
    }


    public String getCurrentVersion() {
        return currentId.getVersion();
    }

    public synchronized void addAnswer(Identifier sourceId, SourceResponseDto answer) {
        sourceLoadContainer.addAnswer(sourceId, answer);
    }

    public synchronized boolean addCallback(Identifier sourceId, SenderCallback<SourceResponseDto> senderCallback) {
        if (sourceLoadContainer.isSourceRequested(sourceId)){
            sourceLoadContainer.addCallback(sourceId, senderCallback);
            return true;
        }else {
            sourceLoadContainer.registerSourceRequest(sourceId);
            return false;
        }
    }

//    public synchronized void registerSourceRequest(Identifier sourceId) {
//        sourceLoadContainer.registerSourceRequest(sourceId);
//    }

//    public synchronized boolean isSourceRequested(Identifier sourceId) {
//        return sourceLoadContainer.isSourceRequested(sourceId);
//    }

    public void error(Exception e) {
        variableCallback.error(e);
    }
}
