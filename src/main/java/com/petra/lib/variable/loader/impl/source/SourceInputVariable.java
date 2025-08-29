package com.petra.lib.variable.loader.impl.source;

import com.petra.lib.constructor.model.SourceInputVariableModel;
import com.petra.lib.variable.enums.Multiplicity;

public class SourceInputVariable {

    private final Long sourceVariable;
    private final Long currentBlockVariable;
    private final String extractionString;
    private final String sourceValueName;
    private final Multiplicity sourceValueMultiplicity;


    public SourceInputVariable(SourceInputVariableModel sourceInputVariableModel) {
        this.sourceVariable = sourceInputVariableModel.getSourceVariable();
        this.currentBlockVariable = sourceInputVariableModel.getCurrentBlockVariable();
        this.extractionString = sourceInputVariableModel.getExtractionString();
        this.sourceValueName = sourceInputVariableModel.getSourceValueName();
        this.sourceValueMultiplicity = sourceInputVariableModel.getSourceValueMultiplicity();
    }

    public Long getSourceVariable() {
        return sourceVariable;
    }

    public Long getCurrentBlockVariable() {
        return currentBlockVariable;
    }

    public String getExtractionString() {
        return extractionString;
    }

    public String getSourceValueName() {
        return sourceValueName;
    }

    public Multiplicity getSourceValueMultiplicity() {
        return sourceValueMultiplicity;
    }

}
