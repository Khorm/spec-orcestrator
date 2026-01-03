package com.petra.lib.variable.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.petra.lib.variable.enums.Multiplicity;

import java.util.Base64;

public class ValueModel {
    private Long id;
    private String name;
    private String jsonVariable;
    private Multiplicity multiplicity;

    public ValueModel(Long id, String name, Multiplicity multiplicity, String jsonVariable) {
        this.id = id;
        this.name = name;
        if (jsonVariable != null) {
            this.jsonVariable = Base64.getEncoder().encodeToString(jsonVariable.getBytes());
        }
        this.multiplicity = multiplicity;
    }

    /**
     * ТОЛЬКО ДЛЯ JSON
     */
    @JsonCreator
    ValueModel(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("multiplicity") String multiplicity,
            @JsonProperty("jsonVariable") String jsonVariable) {
        this.id = id;
        this.name = name;
        this.jsonVariable = jsonVariable;
        this.multiplicity = Multiplicity.valueOf(multiplicity);
    }


    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("jsonVariable")
    String getJsonVariable() {
        if (jsonVariable == null || jsonVariable.isEmpty()) {
            return null;
        }
        return jsonVariable;
    }

    @JsonIgnore
    public String getJsonValue() {
        if (jsonVariable == null || jsonVariable.isEmpty()) {
            return null;
        }
        return new String(Base64.getDecoder().decode(jsonVariable.getBytes()));
    }

//    public void setJsonVariable(String jsonVariable) {
//        this.jsonVariable = Base64.getEncoder().encodeToString(jsonVariable.getBytes());
//    }

    @JsonProperty("multiplicity")
    public String getMultiplicityName() {
        return multiplicity.name();
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

}
