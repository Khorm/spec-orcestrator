package com.petra.lib.context;

public enum ContextState {

    //блок выполняет подтверждение о выполнении
    ANSWERED(null),

    //воркфлоу выполнила все входящие в нее блоки
    EXECUTED(ANSWERED),

    //начало исполнения воркфлоу
    STARTED(EXECUTED),

    ;

    private final ContextState next;

    ContextState(ContextState next){
        this.next = next;
    }

    public ContextState getNext(){
        return next;
    }

}
