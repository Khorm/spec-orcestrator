package com.petra.lib.variable.loader.impl;

import com.petra.lib.constructor.model.ValueLoaderDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.value.Value;

import java.util.List;

class EmptyLoader extends LoaderAbs {
    protected EmptyLoader(ThreadController threadController, ValueLoaderDto valueModel, List<Long> parents, List<ValueLoader> children) {
        super(threadController, valueModel, parents, children);
    }

    @Override
    protected ValueDto executeLoad(ValueContext context) {
        return null;
    }
//    private final long variableId;
//    private final String name;
//    private final Multiplicity multiplicity;

//    EmptyLoader(ThreadController threadController, ValueModelDto valueModel,
//                List<Long> parents, List<LoaderAbs> children) {
//        super(threadController, valueModel);
//    }
//
//
//    @Override
//    protected void executeLoad(ValueContext context) {
//        ValueModel valueModel = new ValueModel(getValueModel().getId(), getValueModel().getName(),
//                getValueModel().getMultiplicity(),null);
//        context.registerLoadedValue(ValueFactory.createValue(valueModel), this);
//    }
}
