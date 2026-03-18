package com.petra.lib.z_user_package.source_handlers;

import com.petra.lib.operation.operations.executor.UserActionContext;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

@Service("ScdAction")
public class Secnd_Activivty implements UserActionHandler {
    @Override
    public void execute(UserActionContext variableUserActionContext) {
        System.out.println("Secon Activity");
        Long var =variableUserActionContext.getValue("TstVarTwo", Long.class);
        variableUserActionContext.setValue("SceOutVar",  new Location(var, "Id by " +var));
    }

    @Override
    public Isolation getTransactionIsolationLevel() {
        return Isolation.DEFAULT;
    }
}
