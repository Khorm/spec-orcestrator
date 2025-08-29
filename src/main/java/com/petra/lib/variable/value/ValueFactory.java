package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.model.ValueModel;

public class ValueFactory {

    public static Value createValue(ValueModel valueModel) {
        return createValue(valueModel.getId(), valueModel.getName(), valueModel.getMultiplicity(), valueModel.getJsonValue());
    }

    public static Value createValue(Long id, String name, Multiplicity multiplicity, Object value) {
        try {
            switch (multiplicity) {
                case SINGLE:
                    return new ObjectValue(id, new ObjectMapper().writeValueAsString(value), multiplicity, name);
                case COLLECTION:
                    return new ArrayValue(new ObjectMapper().writeValueAsString(value), id, name, multiplicity);
                default:
                    throw new IllegalArgumentException("Wrong multiplicity " + multiplicity + " on varibale " + name);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Value createValue(Long id, String name, Multiplicity multiplicity, String valueJson) {
        if (valueJson == null || valueJson.isBlank()) {
            if (multiplicity == Multiplicity.SINGLE) {
                return new SimpleValue(null, id, name, multiplicity);
            } else {
                return new ArrayValue(null, id, name, multiplicity);
            }
        }

        ObjectMapper extractMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = extractMapper.readTree(valueJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (rootNode.isObject()) {
            if (multiplicity != Multiplicity.SINGLE)
                throw new IllegalArgumentException("Wrong multiplicity on variable " + name);
            return new ObjectValue(id, valueJson, multiplicity, name);
        }

        if (rootNode.isArray()) {
            if (multiplicity != Multiplicity.COLLECTION)
                throw new IllegalArgumentException("Wrong multiplicity on variable " + name);
            return new ArrayValue(valueJson, id, name, multiplicity);
        }


        if (multiplicity != Multiplicity.SINGLE)
            throw new IllegalArgumentException("Wrong multiplicity on variable " + name);
        return new SimpleValue(valueJson, id, name, multiplicity);

    }
}
