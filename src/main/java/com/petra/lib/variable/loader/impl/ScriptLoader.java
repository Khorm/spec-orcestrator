package com.petra.lib.variable.loader.impl;

import com.petra.lib.constructor.model.ValueLoaderDto;
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


    ScriptLoader(ValueLoaderDto valueModel,
                 ThreadController threadController,
                 List<Long> parents, List<ValueLoader> children) {
        super(threadController,valueModel, parents, children);
        this.groovyScript = valueModel.getScript();
        this.name = valueModel.getName();
        this.multiplicity = valueModel.getMultiplicity();
    }


    @Override
    protected ValueDto executeLoad(ValueContext context) {

        String valuesScript = getParents().stream().map(aLong -> {
            StringBuilder ret = new StringBuilder();
            ValueDto value = context.getValue(aLong);

            return ret.append("def ")
                    .append(value.getName())
                    .append(" = slurper.parseText('")
                    .append(value.getJsonValue())
                    .append("') ; ").toString();

        }).collect(Collectors.joining());

        String script = "import com.fasterxml.jackson.databind.ObjectMapper; " +
                " def slurper = new groovy.json.JsonSlurper(); "
                + valuesScript
                + groovyScript
                + "; def oj = new ObjectMapper(); "
                + " return oj.writeValueAsString(result); ";

        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        String result = (String) shell.evaluate(script);
        return new ValueDto(getVariableId(), name, multiplicity, result);
    }
}
