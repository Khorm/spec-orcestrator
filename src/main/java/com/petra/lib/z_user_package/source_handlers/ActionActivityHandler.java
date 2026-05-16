package com.petra.lib.z_user_package.source_handlers;

import com.petra.lib.actor.local.activity.UserActivityContext;
import com.petra.lib.actor.local.activity.UserActivityHandler;
//import com.petra.lib.annotation.WorkflowHandler;
import org.springframework.transaction.annotation.Isolation;

//@WorkflowHandler(name = "test_action_1")
//@Service
public class ActionActivityHandler implements UserActivityHandler {
    @Override
    public void execute(UserActivityContext variableUserActivityContext) {
        variableUserActivityContext.getValue("variableTwo", String.class);
    }

    @Override
    public Isolation getTransactionIsolationLevel() {
        return null;
    }
//    @Override
//    public void execute(VariableUserContext variableUserContext) {
//        System.out.println("EXECUTION test_action_1");
//    }
}
