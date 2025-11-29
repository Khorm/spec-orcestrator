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
    public static ActivityOperationService createBlockOperationService(
            ThreadController threadController,
            AnswerOperation answerOperation,
            BlockUserOperation blockUserOperation,
            Sender sender,
            String serviceName) {
        if (threadController == null) {
            throw new IllegalArgumentException("ThreadController must not be null");
        }
        if (answerOperation == null) {
            throw new IllegalArgumentException("AnswerOperation must not be null");
        }
        if (sender == null) {
            throw new IllegalArgumentException("Sender must not be null");
        }
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("ServiceName must not be null or blank");
        }

        return new ActivityOperationService(threadController, blockUserOperation, answerOperation);
    }

    /**
     * Создаёт сервис операций для выполнения всего workflow (WorkflowOperationService).
     *
     * @param threadController контроллер управления асинхронными операциями (не null)
     * @param answerOperation операция отправки ответа (не null)
     * @param workflowExecutingOperation операция выполнения шагов workflow (не null)
     * @return настроенный экземпляр {@link WorkflowOperationService}
     *
     * @throws IllegalArgumentException если любой из параметров равен null
     */
    public static WorkflowOperationService createWorkflowOperationService(
            ThreadController threadController,
            WorkflowExecutingOperation workflowExecutingOperation,
            AnswerOperation answerOperation) {
        if (threadController == null) {
            throw new IllegalArgumentException("ThreadController must not be null");
        }
        if (workflowExecutingOperation == null) {
            throw new IllegalArgumentException("WorkflowExecutingOperation must not be null");
        }
        if (answerOperation == null) {
            throw new IllegalArgumentException("AnswerOperation must not be null");
        }

        return new WorkflowOperationService(threadController, workflowExecutingOperation, answerOperation);
    }
}
