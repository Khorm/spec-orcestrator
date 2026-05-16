package com.petra.lib.variable.loader.impl;

import com.petra.lib.constructor.model.ValueLoaderDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import java.util.List;
import java.util.Optional;

class InputLoader extends LoaderAbs {

    private final Optional<String> extractionString;


    InputLoader(ThreadController threadController, ValueLoaderDto valueModel,
                List<Long> parents, List<ValueLoader> children) {
        super(threadController, valueModel,parents, children);
        this.extractionString = valueModel.getExtractionString() == null || valueModel.getExtractionString().isBlank() ?
                Optional.empty() : Optional.of(valueModel.getExtractionString());
    }

    @Override
    protected ValueDto executeLoad(ValueContext context) {
        ValueDto producerValue = context.getValue(getValueModel().getInputValueId());
        if (producerValue.getMultiplicity() != getValueModel().getMultiplicity()) {
            throw new IllegalArgumentException("Wrong producer multiplicity for value " + getValueModel().getName());
        }
        String newJsonValue;
        if (extractionString.isPresent()) {
            newJsonValue = producerValue.getExtractedJsonValue(extractionString.get());
        } else {
            newJsonValue = producerValue.getJsonValue();
        }

        return new ValueDto(getVariableId(), getValueModel().getName(),
                getValueModel().getMultiplicity(),newJsonValue);
    }

}
