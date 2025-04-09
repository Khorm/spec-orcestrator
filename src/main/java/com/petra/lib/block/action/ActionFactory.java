package com.petra.lib.block.action;

import com.petra.lib.block.Block;
import com.petra.lib.block.action.executor.UserExecutor;
import com.petra.lib.block.action.executor.handler.UserActionHandler;
import com.petra.lib.block.action.finisher.FinishManager;
import com.petra.lib.block.action.repo.ActionRepo;
import com.petra.lib.block.action.repo.ActivityRepoImpl;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.sender.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.VariableFactory;

public class ActionFactory {

    public static Block createAction(ActionModel actionModel, ThreadController threadController,
                                     TransactionManager transactionManager, Sender sender,
                                     UserActionHandler userActionHandler){
        Identifier actionId = new Identifier(actionModel.getId(), actionModel.getVersion());
        ActionRepo actionRepo = new ActivityRepoImpl(transactionManager);
        UserExecutor userExecutor = new UserExecutor(transactionManager, userActionHandler, actionRepo);
        FinishManager finishManager = new FinishManager(sender, transactionManager,actionRepo);

        return new Action(
                threadController,
                actionId,
                actionRepo,
                userExecutor,
                finishManager,
                transactionManager,
                sender
        );
    }
}
