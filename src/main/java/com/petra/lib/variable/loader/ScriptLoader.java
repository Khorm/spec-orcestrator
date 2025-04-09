package com.petra.lib.variable.loader;

import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.value.Multiplicity;
import com.petra.lib.variable.value.Value;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.List;
import java.util.stream.Collectors;

class ScriptLoader implements ValueLoader {

    private final String groovyScript;
    private final Long variableId;
    private final String name;
    private final Multiplicity multiplicity;
    private final List<ValueLoader> childValues;
    private final List<Long> parentValues;

    ScriptLoader(String groovyScript, Long variableId, String name,
                 Multiplicity multiplicity, List<ValueLoader> childValues, List<Long> parentValues) {
        this.groovyScript = groovyScript;
        this.variableId = variableId;
        this.name = name;
        this.multiplicity = multiplicity;
        this.childValues = childValues;
        this.parentValues = parentValues;
    }

    @Override
    public void load(ValueContext context) {
        if (!context.areValuesLoaded(parentValues)) return;

        String valuesScript = parentValues.stream().map(aLong -> {
            StringBuilder ret = new StringBuilder();
            Value value = context.getCurentValue(aLong);
            return ret.append("def ")
                    .append(value.getValue())
                    .append(" = '")
                    .append(value.getJsonValue())
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
        context.setValue(new Value(variableId, result, multiplicity, name));

        try {
            for (ValueLoader valueLoader : childValues) {
                valueLoader.load(context);
            }
        } catch (Exception e) {
            context.error(e);
        }
    }
}
