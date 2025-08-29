package com.petra.lib.constructor.model;

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

    public Multiplicity getMultiplicity() {
        return Multiplicity.valueOf(multiplicity);
    }
}
