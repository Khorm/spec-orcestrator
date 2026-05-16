package com.petra.lib.z_user_package.source_handlers;

import com.petra.lib.actor.local.activity.UserActivityContext;
import com.petra.lib.actor.local.activity.UserActivityHandler;
import org.springframework.transaction.annotation.Isolation;

//@Service("ScdAction")
public class Secnd_Activivty implements UserActivityHandler {
    @Override
    public void execute(UserActivityContext variableUserActivityContext) {
        System.out.println("Secon Activity");
        Long var = variableUserActivityContext.getValue("TstVarTwo", Long.class);
        variableUserActivityContext.setValue("SceOutVar",  new Location(var, "Id by " +var));
    }

    @Override
    public Isolation getTransactionIsolationLevel() {
        return Isolation.DEFAULT;
    }
}
