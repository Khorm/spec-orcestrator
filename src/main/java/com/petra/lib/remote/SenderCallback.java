package com.petra.lib.remote;


public interface SenderCallback{
    void answer(MessageResponse messageResponse);
    void error(Exception e, MessageResponse messageResponse);
}
