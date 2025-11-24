package com.petra.lib.context.repo;

import com.petra.lib.transaction.TransactionManager;

public final class RepoFactory {

    public static ContextRepo createBlockRepo(TransactionManager transactionManager){
        return new ContextRepoImpl(transactionManager);
    }

}
