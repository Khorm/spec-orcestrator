package com.petra.lib.operation;

import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.BlockExecutingOperation;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;

import java.util.Optional;

public final class OperationConstructor {

//    /**
//     * Создаёт сервис операций для выполнения блока (BlockOperationService).
//     *
//     * @param threadController контроллер управления асинхронными операциями (не null)
//     * @param answerOperation операция отправки ответа (не null)
//     * @param blockUserOperation операция исполнения пользователя (не null)
//     * @param sender компонент для отправки сообщений (не null)
//     * @param serviceName имя текущего сервиса, используется при маршрутизации (не null)
//     * @return настроенный экземпляр {@link ActivityOperationService}
//     *
//     * @throws IllegalArgumentException если любой из обязательных параметров (кроме userActionHandlerMap) равен null
//     */
//    public static AsyncOperationService createActionOperationService(
//            ThreadController threadController, TransactionManager transactionManager) {
//        return new AsyncOperationService(threadController, transactionManager);
//    }
//
//    /**
//     * Создаёт сервис операций для выполнения всего workflow (WorkflowOperationService).
//     *
//     * @param threadController контроллер управления асинхронными операциями (не null)
//     * @return настроенный экземпляр {@link WorkflowOperationService}
//     *
//     * @throws IllegalArgumentException если любой из параметров равен null
//     */
//    public static AsyncOperationService createWorkflowOperationService(
//            ThreadController threadController, TransactionManager transactionManager) {
//        if (threadController == null) {
//            throw new IllegalArgumentException("ThreadController must not be null");
//        }

    /// /        if (workflowExecutingOperation == null) {
    /// /            throw new IllegalArgumentException("WorkflowExecutingOperation must not be null");
    /// /        }
    /// /        if (answerOperation == null) {
    /// /            throw new IllegalArgumentException("AnswerOperation must not be null");
    /// /        }
//
//        return new AsyncOperationService(threadController, transactionManager);
//    }
    public static OperationService createAsyncOperationService(ThreadController controller,
                                                               TransactionManager transactionManager,
                                                               Operation ... operations) {

        return new AsyncOperationService(controller, transactionManager, operations);
    }

    public static OperationService createSyncOperationService(TransactionManager transactionManager,
                                                              Operation ... operations) {
        return new SyncOperationService(transactionManager, operations);
    }

    public static OperationService createUserService(ThreadController controller, TransactionManager transactionManager){
        return new AsyncOperationService(controller, transactionManager);
    }


}
