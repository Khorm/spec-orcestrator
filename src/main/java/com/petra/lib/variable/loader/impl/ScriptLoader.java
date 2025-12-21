package com.petra.lib.variable.loader.impl;

import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.container.ValueModel;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.List;
import java.util.stream.Collectors;

class ScriptLoader extends LoaderAbs {

    private final String groovyScript;
    private final Long variableId;
    private final String name;
    private final Multiplicity multiplicity;


    ScriptLoader(String groovyScript, Long variableId, String name,
                 Multiplicity multiplicity, List<ValueLoader> childValues, List<Long> parentValues, ThreadController threadController) {
        super(childValues, parentValues, threadController);
        this.groovyScript = groovyScript;
        this.variableId = variableId;
        this.name = name;
        this.multiplicity = multiplicity;
    }


    @Override
    protected void executeLoad(ValueContext context) {
        String valuesScript = getParentValues().stream().map(aLong -> {
            StringBuilder ret = new StringBuilder();
            Value value = context.getValue(aLong);
            return ret.append("def ")
                    .append(value.getModel().getJsonValue())
                    .append(" = '")
                    .append(value.getModel().getJsonValue())
                    .append("' ; ").toString();

        }).collect(Collectors.joining());

        String script = " def slurper = new groovy.json.JsonSlurper(); "
                + valuesScript
                + groovyScript
                + " ObjectMapper oj = new ObjectMapper(); "
                + " return oj.writeValueAsString(result); ";

        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        String result = (String) shell.evaluate(script);
        ValueModel valueModel = new ValueModel(variableId, name, multiplicity, result);
        context.setValue(ValueFactory.createValue(valueModel), this);
    }
}
