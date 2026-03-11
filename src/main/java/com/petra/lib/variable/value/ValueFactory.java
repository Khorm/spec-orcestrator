package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.container.ValueModel;

import java.util.Base64;

public class ValueFactory {

    public static Value createValue(ValueModel valueModel) {
        if (valueModel.getJsonValue() == null || valueModel.getJsonValue().isBlank()) {
            if (valueModel.getMultiplicity() == Multiplicity.SINGLE) {
                return new SimpleValue(valueModel);
            } else {
                return new ArrayValue(valueModel);
            }
        }

        ObjectMapper extractMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = extractMapper.readTree(valueModel.getJsonValue());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (rootNode.isObject()) {
            if (valueModel.getMultiplicity() != Multiplicity.SINGLE)
                throw new IllegalArgumentException("Wrong multiplicity on variable " + valueModel.getName());
            return new ObjectValue(valueModel);
        }

        if (rootNode.isArray()) {
            if (valueModel.getMultiplicity() != Multiplicity.COLLECTION)
                throw new IllegalArgumentException("Wrong multiplicity on variable " + valueModel.getName());
            return new ArrayValue(valueModel);
        }

        if (valueModel.getMultiplicity() != Multiplicity.SINGLE)
            throw new IllegalArgumentException("Wrong multiplicity on variable " + valueModel.getName());

        return new SimpleValue(valueModel);
    }
}
