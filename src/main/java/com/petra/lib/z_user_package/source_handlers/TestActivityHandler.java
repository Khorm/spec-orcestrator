package com.petra.lib.z_user_package.source_handlers;

//import com.petra.lib.annotation.WorkflowHandler;
import com.petra.lib.actor.local.activity.UserActivityContext;
import com.petra.lib.actor.local.activity.UserActivityHandler;
import org.springframework.transaction.annotation.Isolation;

//@WorkflowHandler(name = "test_source_1")
//@Service("FstAction")
public class TestActivityHandler implements UserActivityHandler {
    @Override
    public void execute(UserActivityContext variableUserActivityContext) {

        System.out.println("FstAction");
        System.out.println("SOURCE USING FstIn " + variableUserActivityContext.getValue("fst", Long.class));
        System.out.println("SOURCE USING ScdIn " + variableUserActivityContext.getValue("scd", Truck.class));
        variableUserActivityContext.setValue("OutFstVal", new Location(1L, "Location one"));
    }

    @Override
    public Isolation getTransactionIsolationLevel() {
        return Isolation.DEFAULT;
    }
//    @Override
//    public void execute(VariableUserContext variableUserContext) {
//        String value = variableUserContext.getExecutionVariable("fst_variable", String.class);
//        System.out.println("Hello world " + value);
//        TestEntity testEntity = new TestEntity();
//        testEntity.setMessage(value);
//        variableUserContext.getEntityManager().persist(testEntity);
//        variableUserContext.putExecutionVariable("scd_variable", "test");
//    }
}
