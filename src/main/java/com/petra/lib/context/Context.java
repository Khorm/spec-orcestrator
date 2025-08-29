package com.petra.lib.context;

import com.petra.lib.variable.container.ValueContainer;

public interface Context {
    void setState(ValueContainer values, ContextState executedState);
    void error(Exception e);
}
