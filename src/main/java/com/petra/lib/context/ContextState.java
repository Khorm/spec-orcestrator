package com.petra.lib.context;

public enum ContextState {

    //контекст только создан и не заполнен
//    CREATING,

    //начало исполнения воркфлоу
   STARTED,

    //происходит загрузка переменных (ЗАГРУЖАЕТСЯ БЕЗ СТАТУСА)
//    WORKFLOW_LOADING_VARIABLES,

    //воркфлоу выполнила все входящие в нее блоки
   EXECUTED,

    /**
     * Response after execution
     */
//    ACTIVITY_EXECUTING,

    //блок выполняет подтверждение о выполнении
    ANSWERED,

}
