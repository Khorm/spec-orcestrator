package com.petra.lib.source;

import com.petra.lib.block.action.executor.handler.UserActionHandler;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.transaction.TransactionManager;

public class SourceFactory {

    public static Source createSource(SourceModel sourceModel, TransactionManager transactionManager, UserActionHandler userActionHandler){
        return new Source(new Identifier(sourceModel.getSourceId(), sourceModel.getSourceVersion()), transactionManager, userActionHandler);
    }

}
