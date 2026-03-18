package com.petra.lib.operation.operations.executor;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.operation.Operation;
import com.petra.lib.operation.OperationService;
import com.petra.lib.operation.operations.AnswerOperation;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.transaction.TransactionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.annotation.Isolation;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Objects;

/**
 * обрабатывает вызов функции юзера
 */
public class BlockUserOperation implements Operation {

    private static final Logger log = LogManager.getLogger(BlockUserOperation.class);
    private final TransactionManager transactionManager;
    private final ContextState CURRENT_STATE = ContextState.EXECUTED;

    private final Map<Identifier, UserActionHandler> userHandlers;
    private final OperationService operationService;

    public BlockUserOperation(TransactionManager transactionManager, Map<Identifier,
            UserActionHandler> userHandlers, OperationService operationService) {
        this.transactionManager = transactionManager;
        this.userHandlers = userHandlers;
        this.operationService = operationService;
        log.debug("BlockUserOperation initialized with {} user handlers", userHandlers.size());
    }


    /**
     * В одной транзакции выставляет стейт и обработывает пользоватский обработчик.
     *
     * @param blockContext - current execution context
     */
    public void execute(Context blockContext) {
        UserActionHandler userActionHandler = userHandlers.get(blockContext.getCurrentBlockId());
        transactionManager.executeInTransaction(transaction -> {
            try {
                log.debug("Starting transaction for BlockUserOperation");

                EntityManager entityManager = EntityManagerFactoryUtils
                        .getTransactionalEntityManager(Objects.requireNonNull(transaction.getEntityManagerFactory()));

                UserActionContextImpl userContext = new UserActionContextImpl(entityManager,
                        blockContext.getContextInputValues().getValues(), blockContext.getContextOutValues());
                userActionHandler.execute(userContext);
                blockContext.lockAndLoad();
                boolean isResultSet = blockContext.setState(CURRENT_STATE);
                if (!isResultSet){
                    blockContext.saveRepeat();
                    operationService.executeState(blockContext);
                    return;
                }
                blockContext.setOutValues(userContext.getOutputValues());
                blockContext.unlockAndSave();
                log.debug("Context saved successfully for scenarioId: {}", blockContext.getScenarioId());
                operationService.executeState(blockContext);

            } catch (Exception e) {
                log.error("Exception occurred during BlockUserOperation execution for scenarioId: {}, blockId: {}: {}",
                        blockContext.getScenarioId(), blockContext.getCurrentBlockId(), e.getMessage(), e);

                blockContext.saveError(e);
            }
        }, userActionHandler.getTransactionIsolationLevel());

    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }

}
