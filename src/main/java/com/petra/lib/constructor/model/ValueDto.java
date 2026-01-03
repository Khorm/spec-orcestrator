package com.petra.lib.constructor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petra.lib.variable.container.ValueModel;
import com.petra.lib.variable.enums.Multiplicity;

public class ValueDto {
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

    public ValueModel createEmptyModel() {
        return new ValueModel(id, name, getMultiplicityEnm(), null);
    }
}
