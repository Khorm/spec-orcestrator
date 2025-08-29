package com.petra.lib.context.block.operations.executor;

import com.petra.lib.context.ContextState;
import com.petra.lib.context.block.BlockContext;
import com.petra.lib.context.operation.Operation;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.container.ValueContainer;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.annotation.Isolation;

import javax.persistence.EntityManager;
import java.util.Objects;

/**
 * обрабатывает вызов функции юзера
 */
public class BlockUserOperation implements Operation<BlockContext> {

    private final TransactionManager transactionManager;
    private final ContextState CURRENT_STATE = ContextState.EXECUTED;

    public BlockUserOperation(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }


    /**
     * В одной транзакции выставляет стейт и обработывает пользоватский обработчик.
     *
     * @param blockContext - current execution context
     * @throws Exception
     */
    public void execute(BlockContext blockContext) {
        transactionManager.executeInTransaction(transaction -> {
            try {
                EntityManager entityManager = EntityManagerFactoryUtils
                        .getTransactionalEntityManager(Objects.requireNonNull(transaction.getEntityManagerFactory()));

                ValueContainer userContextContainer = blockContext.getContextInputContainer();
                UserActionContextImpl userContext = new UserActionContextImpl(entityManager, userContextContainer,
                        blockContext.getConsumer().getContextOuterValues());
                blockContext.getConsumer().getUserActionHandler().execute(userContext);
                blockContext.setState(userContextContainer, CURRENT_STATE);
            } catch (Exception e) {
                blockContext.error(e);
            }
        }, Isolation.SERIALIZABLE);

    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }

}
