package com.petra.lib.operation.operations.executor;

import com.petra.lib.context.Context;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.operation.Operation;
import com.petra.lib.operation.OperationService;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.transaction.TransactionManager;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.annotation.Isolation;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Objects;

/**
 * обрабатывает вызов функции юзера
 */
public class BlockUserOperation implements Operation {

    private final TransactionManager transactionManager;
    private final ContextState CURRENT_STATE = ContextState.EXECUTED;

    private final Map<Identifier, UserActionHandler> userHandlers;
    private final OperationService operationService;

    public BlockUserOperation(TransactionManager transactionManager, Map<Identifier, UserActionHandler> userHandlers, OperationService operationService) {
        this.transactionManager = transactionManager;
        this.userHandlers = userHandlers;
        this.operationService = operationService;
    }


    /**
     * В одной транзакции выставляет стейт и обработывает пользоватский обработчик.
     *
     * @param blockContext - current execution context
     * @throws Exception
     */
    public void execute(Context blockContext) {
        transactionManager.executeInTransaction(transaction -> {
            try {
                EntityManager entityManager = EntityManagerFactoryUtils
                        .getTransactionalEntityManager(Objects.requireNonNull(transaction.getEntityManagerFactory()));

                UserActionContextImpl userContext = new UserActionContextImpl(entityManager,
                        blockContext.getContextValues().getValues());
                userHandlers.get(blockContext.getCurrentBlockId()).execute(userContext);
                blockContext.setState(CURRENT_STATE);
                blockContext.setOutValues(userContext.getContextContainer());
                blockContext.save();
            } catch (Exception e) {
                blockContext.saveError(e);
                operationService.executeState(blockContext);
            }
        }, Isolation.SERIALIZABLE);

    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }

}
