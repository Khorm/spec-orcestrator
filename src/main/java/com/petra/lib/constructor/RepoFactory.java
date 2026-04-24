package com.petra.lib.constructor;

import com.petra.lib.context.block.repo.ContextRepo;
import com.petra.lib.context.block.repo.ContextRepoImpl;
import com.petra.lib.context.workflow.repo.WorkflowContextRepo;
import com.petra.lib.context.workflow.repo.WorkflowContextRepoImpl;
import com.petra.lib.transaction.TransactionManager;

public final class RepoFactory {

    public static ContextRepo createBlockRepo(TransactionManager transactionManager) {
        return new ContextRepoImpl();
    }

    public static WorkflowContextRepo createWorkflowRepo(TransactionManager transactionManager) {
        return new WorkflowContextRepoImpl();
    }

}
