package com.petra.lib.z_user_package.source_handlers.actions;

import com.petra.lib.operation.operations.executor.UserActionContext;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.z_user_package.source_handlers.models.ModelTwo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

@Service("ScdAction")
public class ScdAction implements UserActionHandler {

    @Override
    public void execute(UserActionContext variableUserActionContext) {
        System.out.println("ScdAction start");
        Long longForModel = variableUserActionContext.getValue("longForModel", Long.class);
        String strForModel = variableUserActionContext.getValue("strForModel", String.class);


        ModelTwo modelTwo = new ModelTwo();
        modelTwo.setFst(longForModel);
        modelTwo.setScd(strForModel);

        variableUserActionContext.setValue("outModel", modelTwo);
    }

    @Override
    public Isolation getTransactionIsolationLevel() {
        return Isolation.DEFAULT;
    }
}
