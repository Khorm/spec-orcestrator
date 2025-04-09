package com.petra.lib.variable.loader;

import com.petra.lib.block.model.Identifier;
import com.petra.lib.sender.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.VariableManager;
import com.petra.lib.variable.value.Multiplicity;

import java.util.List;

public class LoaderFactory {
    public static ValueLoader createInputLoader(Long id, String name, Long inputValueId,
                                                List<Long> parents, List<ValueLoader> children){
        return new InputLoader(inputValueId, children, id, parents, name);
    }

    public static ValueLoader createScriptLoader(String groovyScript, Long variableId, String name,
                                                 Multiplicity multiplicity, List<ValueLoader> childValues,
                                                 List<Long> parentValues){
        return new ScriptLoader(groovyScript, variableId, name, multiplicity, childValues, parentValues);
    }

    public static ValueLoader createSourceLoader(List<ValueLoader> childValues, List<Long> parentValues, Sender sender,
                                                 String producerServiceUrl, Identifier sourceId, String sourceUrl, long sourceVariableId,
                                                 long currentVariableId, String currentVariableName, ThreadController threadController,
                                                 VariableManager sourceVariableManager){
        return new SourceLoader(childValues, parentValues, sender, producerServiceUrl, sourceId, sourceUrl, sourceVariableId,
                currentVariableId, currentVariableName, threadController, sourceVariableManager);
    }
}
