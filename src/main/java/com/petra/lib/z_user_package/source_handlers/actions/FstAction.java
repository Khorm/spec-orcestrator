package com.petra.lib.z_user_package.source_handlers.actions;

import com.petra.lib.operation.operations.executor.UserActionContext;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.z_user_package.source_handlers.models.ModelOne;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import java.util.ArrayList;
import java.util.List;

@Service("FstAction")
public class FstAction implements UserActionHandler {
    @Override
    public void execute(UserActionContext variableUserActionContext) {
        System.out.println("FstAction start");
        ModelOne model = variableUserActionContext.getValue("model", ModelOne.class);

        Long id = model.getInnerOne();
        List<String> lst = new ArrayList<>();
        lst.addAll(model.getInnerTwo());

        String name = lst.get(0);

        System.out.println(id + " - " + name);

        variableUserActionContext.setValue("modelFst", id);
        variableUserActionContext.setValue("modelScd", name);

    }

    @Override
    public Isolation getTransactionIsolationLevel() {
        return Isolation.DEFAULT;
    }
}
