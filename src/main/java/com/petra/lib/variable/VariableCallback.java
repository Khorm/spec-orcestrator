package com.petra.lib.variable;

import com.petra.lib.variable.value.ValueContainer;

public interface VariableCallback {
    void loaded(ValueContainer valueContainer);
    void error(Exception e);
}
