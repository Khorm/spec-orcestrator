package com.petra.lib.variable.loader;

import com.petra.lib.variable.context.ValueContext;

public interface ValueLoader {
    void load(ValueContext context) throws Exception;
}
