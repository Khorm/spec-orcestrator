package com.petra.lib.operation.operations.executor;

import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.actor.LocalConsumer;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.operation.Operation;
import com.petra.lib.operation.OperationService;
import com.petra.lib.transaction.TransactionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * обрабатывает вызов функции юзера
 */
public class BlockUserOperation implements Operation {

    private static final Logger log = LogManager.getLogger(BlockUserOperation.class);
    private final TransactionManager transactionManager;
    private final ContextState CURRENT_STATE = ContextState.EXECUTED;

    private final Map<Identifier, LocalConsumer> userHandlers;

    public BlockUserOperation(TransactionManager transactionManager, Map<Identifier,
            LocalConsumer> userHandlers) {
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
        LocalConsumer userActionHandler = userHandlers.get(blockContext.getCurrentBlockId());
        userActionHandler.execute(blockContext, operationService, transactionManager);
    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }

}
