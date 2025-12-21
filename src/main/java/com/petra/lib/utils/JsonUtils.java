package com.petra.lib.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.PetraException;

public final class JsonUtils {


    public static String getExtractedJsonValue(String extractionString, String inputString) {
        if (extractionString == null || extractionString.isBlank()) {
            return inputString;
        }

//        if (model.getMultiplicity() == Multiplicity.COLLECTION) {
//            throw new UnsupportedOperationException("Extraction is not aloud on COLLECTION in variable " + model.getName());
//        }

        ObjectMapper extractMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = extractMapper.readTree(inputString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String[] extractionArr = extractionString.split(".");
        for (String extractValue : extractionArr) {
            rootNode = rootNode.get(extractValue);
            if (rootNode == null) {
                String err = String.format("Wrong name in value parsing. %s is not found in variable %s", extractValue,
                        "test");
                throw new PetraException(err);
            }
        }

        if (rootNode.isObject() || rootNode.isArray()) {
            return rootNode.toString();
        } else {
            return rootNode.asText();
        }
    }
}
