package com.petra.lib.variable.loader.impl;

import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;

import java.util.List;

public abstract class LoaderAbs implements ValueLoader {

    private final List<ValueLoader> childValues;
    private final List<Long> parentValues;

    private final ThreadController threadController;

    protected LoaderAbs(List<ValueLoader> childValues, List<Long> parentValues, ThreadController threadController) {
        this.childValues = childValues;
        this.parentValues = parentValues;
        this.threadController = threadController;
    }

    public void load(ValueContext context){
        threadController.executeLimitedPoolTask(() -> executeLoad(context));
    }

    protected abstract void executeLoad(ValueContext context);

    public List<Long> getParentValues(){
        return parentValues;
    }

    public List<ValueLoader> getChildValues(){
        return childValues;
    }
}
