package com.petra.lib.variable.loader.impl;

import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.value.ValueFactory;

import java.util.List;

class EmptyLoader extends LoaderAbs {
    private final long variableId;
    private final String name;
    private final Multiplicity multiplicity;

    EmptyLoader(long variableId, List<ValueLoader> childValues, List<Long> parentValues, String name,
                Multiplicity multiplicity, ThreadController threadController) {
        super(childValues, parentValues, threadController);
        this.variableId = variableId;
        this.name = name;
        this.multiplicity = multiplicity;
    }


    @Override
    protected void executeLoad(ValueContext context) {
        context.setValue(ValueFactory.createValue(variableId, name, multiplicity, null), this);
    }
}
