package com.petra.lib.operation.operations.executor;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.operation.Operation;
import com.petra.lib.operation.OperationService;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.transaction.TransactionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

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

    public BlockUserOperation(TransactionManager transactionManager, Map<Identifier,
            UserActionHandler> userHandlers) {
        this.transactionManager = transactionManager;
        this.userHandlers = userHandlers;
        log.debug("BlockUserOperation initialized with {} user handlers", userHandlers.size());
    }


    /**
     * В одной транзакции выставляет стейт и обработывает пользоватский обработчик.
     *
     * @param blockContext - current execution context
     */
    public void execute(Context blockContext, OperationService operationService) {
        UserActionHandler userActionHandler = userHandlers.get(blockContext.getCurrentBlockId());
        transactionManager.executeInTransaction(transaction -> {
            try {
                log.debug("Starting transaction for BlockUserOperation");

                EntityManager entityManager = EntityManagerFactoryUtils
                        .getTransactionalEntityManager(Objects.requireNonNull(transaction.getEntityManagerFactory()));

                UserActionContextImpl userContext = new UserActionContextImpl(entityManager,
                        blockContext.getContextInputValues().getValues(), blockContext.getContextOutValues());
                userActionHandler.execute(userContext);
                blockContext.lockAndLoad(transaction);
                boolean isResultSet = blockContext.setState(CURRENT_STATE);
                if (!isResultSet){
                    blockContext.unlockAndDiscard();
                    return;
                }
                blockContext.setOutValues(userContext.getOutputValues());
                blockContext.setExecutionStatus(ExecutionStatus.OK);
                blockContext.unlockAndSave();
                log.debug("Context saved successfully for scenarioId: {}", blockContext.getScenarioId());
                operationService.executeState(blockContext);

            } catch (Exception e) {
                log.error("Exception occurred during BlockUserOperation execution for scenarioId: {}, blockId: {}: {}",
                        blockContext.getScenarioId(), blockContext.getCurrentBlockId(), e.getMessage(), e);
                boolean isResultSet = blockContext.setState(CURRENT_STATE);
                if (!isResultSet){
                    blockContext.unlockAndDiscard();
                    return;
                }
                blockContext.setExecutionStatus(ExecutionStatus.ERROR);
                blockContext.unlockAndSave();
                operationService.executeState(blockContext);
            }
        }, userActionHandler.getTransactionIsolationLevel());

    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }

}
