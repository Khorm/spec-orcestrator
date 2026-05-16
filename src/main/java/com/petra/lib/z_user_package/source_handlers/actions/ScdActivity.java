package com.petra.lib.z_user_package.source_handlers.actions;

import com.petra.lib.actor.local.activity.UserActivityContext;
import com.petra.lib.actor.local.activity.UserActivityHandler;
import com.petra.lib.z_user_package.source_handlers.models.ModelTwo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

@Service("ScdAction")
public class ScdActivity implements UserActivityHandler {

    @Override
    public void execute(UserActivityContext variableUserActivityContext) {
        System.out.println("ScdAction start");
        Long longForModel = variableUserActivityContext.getValue("longForModel", Long.class);
        String strForModel = variableUserActivityContext.getValue("strForModel", String.class);


        ModelTwo modelTwo = new ModelTwo();
        modelTwo.setFst(longForModel);
        modelTwo.setScd(strForModel);

        variableUserActivityContext.setValue("outModel", modelTwo);
    }

    @Override
    public Isolation getTransactionIsolationLevel() {
        return Isolation.DEFAULT;
    }
}
