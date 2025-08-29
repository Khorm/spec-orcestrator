package com.petra.lib.variable.model;

import com.petra.lib.variable.enums.Multiplicity;

public class ValueModel {
    private Long id;
    private String name;
    private String jsonVariable;
    private Multiplicity multiplicity;

    public ValueModel(Long id, String name, Multiplicity multiplicity, String jsonVariable) {
        this.id = id;
        this.name = name;
        this.jsonVariable = jsonVariable;
        this.multiplicity = multiplicity;
    }

    public ValueModel() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getJsonValue() {
        return jsonVariable;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJsonVariable(String jsonVariable) {
        this.jsonVariable = jsonVariable;

    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }
}
