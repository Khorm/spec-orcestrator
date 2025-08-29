package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.PetraException;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.model.ValueModel;

abstract class ValueAbs implements Value {

    protected static final ObjectMapper oj = new ObjectMapper();

    protected final String JSONvalue;
    protected final Long id;
    protected final String name;
    protected final Multiplicity multiplicity;

    ValueAbs(String jsoNvalue, Long id, String name, Multiplicity multiplicity) {
        JSONvalue = jsoNvalue;
        this.id = id;
        this.name = name;
        this.multiplicity = multiplicity;
    }

    public String getExtractedJsonValue(String extractionString) {
        if (extractionString == null || extractionString.isBlank()) {
            return JSONvalue;
        }

        if (multiplicity == Multiplicity.COLLECTION) {
            throw new UnsupportedOperationException("Extraction is not aloud on COLLECTION in variable " + name);
        }

        String[] extractionArr = extractionString.split(".");
        ObjectMapper extractMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = extractMapper.readTree(extractionString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        for (String extractValue : extractionArr) {
            rootNode = rootNode.get(extractValue);
            if (rootNode == null) {
                String err = String.format("Wrong name in value parsing. %s is not found in variable %s", extractValue, name);
                throw new PetraException(err);
            }
        }

        if (rootNode.isObject() || rootNode.isArray()) {
            return rootNode.toString();
        } else {
            return rootNode.asText();
        }
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getJsonValue() {
        return JSONvalue;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    @Override
    public ValueModel getModel() {
        return new ValueModel(
                id,
                name,
                multiplicity,
                JSONvalue
        );
    }
}
