package com.petra.lib.variable;

import com.petra.lib.variable.container.ValueContainer;

/**
 * Интерфейс который после загрузки переменных возвращает управление блоку
 */
public interface VariableCallback {
    void loaded(ValueContainer loadedValues);

    void error(Exception e);
}
