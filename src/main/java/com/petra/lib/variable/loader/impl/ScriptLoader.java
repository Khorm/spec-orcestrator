package com.petra.lib.variable.loader.impl;

import com.petra.lib.constructor.model.ValueModelDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ScriptLoader extends LoaderAbs {

    String groovyScript;
    String name;
    Multiplicity multiplicity;


    ScriptLoader(ValueModelDto valueModel,
                 ThreadController threadController,
                 List<Long> parents, List<ValueLoader> children) {
        super(threadController,valueModel, parents, children);
        this.groovyScript = valueModel.getScript();
        this.name = valueModel.getName();
        this.multiplicity = valueModel.getMultiplicity();
    }


    @Override
    protected Value executeLoad(ValueContext context) {

        String valuesScript = getParents().stream().map(aLong -> {
            StringBuilder ret = new StringBuilder();
            Value value = context.getValue(aLong);
            return ret.append("def ")
                    .append(value.getModel().getName())
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
        ValueDto valueDto = new ValueDto(getVariableId(), name, multiplicity, result);
        return ValueFactory.createValue(valueDto);
    }
}
