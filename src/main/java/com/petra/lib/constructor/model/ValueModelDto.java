package com.petra.lib.constructor.model;

import com.petra.lib.constructor.enums.LoaderType;
import com.petra.lib.variable.enums.Multiplicity;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ValueModelDto {
    /**
     * id консумера
     */
    private Long id;
    private String name;
    private String multiplicity;
    private String loaderType;
    private String extractionString;

    /**
     * Настройки для INPUT
     */
    private Long inputValueId;


    /**
     * Настройки для SCRIPT
     */
    private String script;

    /**
     * Настройки для SOURCE
     */
    private Long sourceId;
    private String sourceVersion;
    private String sourceName;
    private String sourceServicePath;

    /**
     * Параметры(продюсеры) для SOURCE и SCRIPT
     */
    private Collection<SourceInputVariableModel> sourceInputVariableModels;


    public LoaderType getLoaderType() {
        return LoaderType.valueOf(loaderType);
    }

    public Multiplicity getMultiplicity() {
        return Multiplicity.valueOf(multiplicity);
    }


    public boolean isChildOf(Long variableId){
        if (getLoaderType() == LoaderType.SOURCE_LOADER || getLoaderType() == LoaderType.SCRIPT_LOADER){
            for (SourceInputVariableModel variable : sourceInputVariableModels){
                if (variable.getProdeucerVariable().equals(variableId)){
                    return true;
                }
            }
        }

        if (getLoaderType() == LoaderType.INPUT_LOADER && inputValueId.equals(variableId)){
            return true;
        }

        return false;
    }



}
