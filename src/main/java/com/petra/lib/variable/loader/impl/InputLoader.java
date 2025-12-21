package com.petra.lib.variable.loader.impl;

import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.container.ValueModel;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import java.util.List;
import java.util.Optional;

class InputLoader extends LoaderAbs {

    private final long inputValueId;
    private final Optional<String> extractionString;
    private final ValueModel valueModel;

    InputLoader(Long inputValueId, List<ValueLoader> childrenValues,
                List<Long> parentValues, String extractionString,
                ThreadController threadController, ValueModel valueModel) {
        super(childrenValues, parentValues, threadController);
        this.inputValueId = inputValueId;
        this.valueModel = valueModel;
        this.extractionString = extractionString == null || extractionString.isBlank() ?
                Optional.empty() : Optional.of(extractionString);
    }

    @Override
    protected void executeLoad(ValueContext context) {
        Value producerValue = context.getValue(inputValueId);
        if (producerValue.getMultiplicity() != valueModel.getMultiplicity()) {
            throw new IllegalArgumentException("Wrong producer multiplicity for value " + valueModel.getName());
        }
        String newJsonValue;
        if (extractionString.isPresent()) {
            newJsonValue = producerValue.getExtractedJsonValue(extractionString.get());
        } else {
            newJsonValue = producerValue.getModel().getJsonValue();
        }
        Value contextValue = ValueFactory.createValue(valueModel);
        contextValue.setJsonValue(newJsonValue);

        context.setValue(contextValue, this);
    }

}
