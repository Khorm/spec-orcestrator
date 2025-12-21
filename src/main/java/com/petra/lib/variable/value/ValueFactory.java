package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.container.ValueModel;

import java.util.Base64;

public class ValueFactory {

//    public static Value createValue(ValueModel valueModel) {
//        return createValue(valueModel.getId(), valueModel.getName(), valueModel.getMultiplicity(), valueModel.getJsonValue());
//    }

//    public static Value createValue(ValueModel valueModel) {
//        switch (valueModel.getMultiplicity()) {
//            case SINGLE:
//                return new ObjectValue(valueModel);
//            case COLLECTION:
//                return new ArrayValue(valueModel);
//            default:
//                throw new IllegalArgumentException("Wrong multiplicity " + valueModel.getMultiplicity()
//                        + " on varibale " + valueModel.getName());
//        }
//    }

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
        System.out.println("valueJson: " + valueModel.getJsonValue());
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
        System.out.println("rootNode: " + rootNode.toString());

        if (valueModel.getMultiplicity() != Multiplicity.SINGLE)
            throw new IllegalArgumentException("Wrong multiplicity on variable " + valueModel.getName());
        return new SimpleValue(valueModel);

    }
}
