package com.petra.lib.constructor.model;

import com.petra.lib.variable.enums.Multiplicity;

public class SourceInputVariableModel {
    private Long sourceVariable;
    private Long producerVariable;
    private String extractionString;
    private String sourceValueName;
    private String sourceValueMultiplicity;

    public Long getSourceVariable() {
        return sourceVariable;
    }

    public Long getProducerVariable() {
        return producerVariable;
    }

    public String getExtractionString() {
        return extractionString;
    }

    public String getSourceValueName() {
        return sourceValueName;
    }

    public Multiplicity getSourceValueMultiplicity() {
        return Multiplicity.valueOf(sourceValueMultiplicity);
    }
}
