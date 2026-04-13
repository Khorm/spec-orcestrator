package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.container.ValueDto;

public class ValueFactory {

    public static Value createValue(ValueDto valueDto) {
        if (valueDto.getJsonValue() == null || valueDto.getJsonValue().isBlank()) {
            if (valueDto.getMultiplicity() == Multiplicity.SINGLE) {
                return new SimpleValue(valueDto);
            } else {
                return new ArrayValue(valueDto);
            }
        }

        ObjectMapper extractMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = extractMapper.readTree(valueDto.getJsonValue());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (rootNode.isObject()) {
            if (valueDto.getMultiplicity() != Multiplicity.SINGLE)
                throw new IllegalArgumentException("Wrong multiplicity on variable " + valueDto.getName());
            return new ObjectValue(valueDto);
        }

        if (rootNode.isArray()) {
            if (valueDto.getMultiplicity() != Multiplicity.COLLECTION)
                throw new IllegalArgumentException("Wrong multiplicity on variable " + valueDto.getName());
            return new ArrayValue(valueDto);
        }

        if (valueDto.getMultiplicity() != Multiplicity.SINGLE)
            throw new IllegalArgumentException("Wrong multiplicity on variable " + valueDto.getName());

        return new SimpleValue(valueDto);
    }
}
