package com.petra.lib.operation.operations.executor.handler;

import com.petra.lib.operation.operations.executor.UserActionContext;
import org.springframework.transaction.annotation.Isolation;

public interface UserActionHandler {

    void execute(UserActionContext variableUserActionContext);

    Isolation getTransactionIsolationLevel();
}
