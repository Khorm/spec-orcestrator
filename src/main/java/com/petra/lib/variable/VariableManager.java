package com.petra.lib.variable;

import com.petra.lib.block.model.Identifier;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;

import java.util.List;
import java.util.UUID;

public class VariableManager {
    private final  List<ValueLoader> rootValueLoaders;
    private final long valuesCount;
    private final Identifier ownerId;

    public VariableManager(List<ValueLoader> rootValueLoaders, long valuesCount, Identifier ownerId) {
        this.rootValueLoaders = rootValueLoaders;
        this.valuesCount = valuesCount;
        this.ownerId = ownerId;
    }

    public void execute(String inputValues, UUID scenarioId, VariableCallback variableCallback)  {
        try {
            ValueContext valueContext = new ValueContext(inputValues, variableCallback, valuesCount, scenarioId, ownerId);
            for (ValueLoader valueLoader : rootValueLoaders) {
                valueLoader.load(valueContext);
            }
        }catch (Exception e){
            variableCallback.error(e);
        }
    }
}
