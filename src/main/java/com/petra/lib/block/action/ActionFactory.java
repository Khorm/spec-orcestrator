package com.petra.lib.block.action;

import com.petra.lib.block.Block;
import com.petra.lib.context.block.operations.executor.BlockUserOperation;
import com.petra.lib.context.block.operations.executor.handler.UserActionHandler;
import com.petra.lib.context.block.operations.AnswerOperation;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.context.repo.ContextRepoImpl;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;

public class ActionFactory {

    public static Block createAction(ActionModel actionModel, ThreadController threadController,
                                     TransactionManager transactionManager, Sender sender,
                                     UserActionHandler userActionHandler){
        Identifier actionId = new Identifier(actionModel.getId(), actionModel.getVersion());
        ContextRepo contextRepo = new ContextRepoImpl(transactionManager);
        BlockUserOperation blockUserOperation = new BlockUserOperation(transactionManager, userActionHandler, contextRepo);
        AnswerOperation answerOperation = new AnswerOperation(sender, transactionManager, contextRepo);

        return new Action(
                threadController,
                actionId,
                contextRepo,
                blockUserOperation,
                answerOperation,
                transactionManager,
                sender
        );
    }
}
