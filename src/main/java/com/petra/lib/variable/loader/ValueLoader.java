package com.petra.lib.variable.loader;

import com.petra.lib.variable.context.ValueContext;

import java.util.Collection;
import java.util.List;

public interface ValueLoader {
    void load(ValueContext context);

    Long getVariableId();
    String getVariableName();
    Collection<Long> getParents();


}
