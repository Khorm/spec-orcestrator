package com.petra.lib.operation;

import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.WorkflowExecutingOperation;
import com.petra.lib.operation.operations.executor.BlockUserOperation;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;

public final class OperationConstructor {

    /**
     * Создаёт сервис операций для выполнения блока (BlockOperationService).
     *
     * @param threadController контроллер управления асинхронными операциями (не null)
     * @param answerOperation операция отправки ответа (не null)
     * @param blockUserOperation операция исполнения пользователя (не null)
     * @param sender компонент для отправки сообщений (не null)
     * @param serviceName имя текущего сервиса, используется при маршрутизации (не null)
     * @return настроенный экземпляр {@link ActivityOperationService}
     *
     * @throws IllegalArgumentException если любой из обязательных параметров (кроме userActionHandlerMap) равен null
     */
    public static OperationService createActionOperationService(
            ThreadController threadController) {
        return new OperationService(threadController);
    }

    /**
     * Создаёт сервис операций для выполнения всего workflow (WorkflowOperationService).
     *
     * @param threadController контроллер управления асинхронными операциями (не null)
     * @return настроенный экземпляр {@link WorkflowOperationService}
     *
     * @throws IllegalArgumentException если любой из параметров равен null
     */
    public static OperationService createWorkflowOperationService(
            ThreadController threadController) {
        if (threadController == null) {
            throw new IllegalArgumentException("ThreadController must not be null");
        }
//        if (workflowExecutingOperation == null) {
//            throw new IllegalArgumentException("WorkflowExecutingOperation must not be null");
//        }
//        if (answerOperation == null) {
//            throw new IllegalArgumentException("AnswerOperation must not be null");
//        }

        return new OperationService(threadController);
    }
}
