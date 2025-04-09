package com.petra.lib.sender;

public interface SenderCallback<T> {

    void answer(T dto);
    void error(Exception e);
}
