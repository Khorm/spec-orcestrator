package com.petra.lib.z_user_package.source_handlers;

import com.petra.lib.context.block.operations.executor.UserActionContext;
import com.petra.lib.context.block.operations.executor.handler.UserActionHandler;
import com.petra.lib.annotation.WorkflowHandler;
import org.springframework.stereotype.Service;

@WorkflowHandler(name = "test_action_1")
@Service
public class ActionActionHandler implements UserActionHandler {
    @Override
    public void execute(UserActionContext variableUserActionContext) {
        variableUserActionContext.getValue("variableTwo", String.class);
    }
//    @Override
//    public void execute(VariableUserContext variableUserContext) {
//        System.out.println("EXECUTION test_action_1");
//    }
}
