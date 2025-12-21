package com.petra.lib.variable.loader.impl;

import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.container.ValueModel;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.value.ValueFactory;

import java.util.List;

class EmptyLoader extends LoaderAbs {
//    private final long variableId;
//    private final String name;
//    private final Multiplicity multiplicity;

    private final ValueModel valueModel;
    EmptyLoader(List<ValueLoader> childValues, List<Long> parentValues,
                ThreadController threadController, ValueModel valueModel) {
        super(childValues, parentValues, threadController);
        this.valueModel = valueModel;
    }


    @Override
    protected void executeLoad(ValueContext context) {
        context.setValue(ValueFactory.createValue(valueModel), this);
    }
}
