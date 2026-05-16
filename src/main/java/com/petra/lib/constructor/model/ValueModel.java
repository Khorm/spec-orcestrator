package com.petra.lib.constructor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.enums.Multiplicity;

public class ValueModel {
    private Long id;
    private String name;
    private String multiplicity;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public Multiplicity getMultiplicityEnm() {
        return Multiplicity.valueOf(multiplicity);
    }

    public String getMultiplicity() {
        return multiplicity;
    }

    public ValueDto createEmptyModel() {
        return new ValueDto(id, name, getMultiplicityEnm(), null);
    }
    public ValueDto createFillModel(Object value) {
        ObjectMapper om = new ObjectMapper();
        try {
            return new ValueDto(id, name, getMultiplicityEnm(), om.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
