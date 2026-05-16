package com.petra.lib.z_user_package.source_handlers.actions;

import com.petra.lib.actor.local.activity.UserActivityContext;
import com.petra.lib.actor.local.activity.UserActivityHandler;
import com.petra.lib.z_user_package.source_handlers.models.ModelOne;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import java.util.ArrayList;
import java.util.List;

@Service("FstAction")
public class FstActivity implements UserActivityHandler {
    @Override
    public void execute(UserActivityContext variableUserActivityContext) {
        System.out.println("FstAction start");
        ModelOne model = variableUserActivityContext.getValue("model", ModelOne.class);

        Long id = model.getInnerOne();
        List<String> lst = new ArrayList<>();
        lst.addAll(model.getInnerTwo());

        String name = lst.get(0);

        System.out.println(id + " - " + name);

        variableUserActivityContext.setValue("modelFst", id);
        variableUserActivityContext.setValue("modelScd", name);

    }

    @Override
    public Isolation getTransactionIsolationLevel() {
        return Isolation.DEFAULT;
    }
}
