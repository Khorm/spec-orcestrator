package com.petra.lib.z_user_package.source_handlers.actions;

import com.petra.lib.actor.local.activity.UserActivityContext;
import com.petra.lib.actor.local.activity.UserActivityHandler;
import com.petra.lib.z_user_package.source_handlers.models.ModelThree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

@Service("ThreeAction")
public class ThirdActivity implements UserActivityHandler {
    @Override
    public void execute(UserActivityContext variableUserActivityContext) {
        System.out.println("ThirdAction start");
        ModelThree mdlThree = variableUserActivityContext.getValue("InnerModel", ModelThree.class);

        System.out.println("fst " + mdlThree.getFst().getFst() + " | " + mdlThree.getFst().getScd() + " " +
                mdlThree.getScd().getFst() + " | " + mdlThree.getScd().getScd());
//        ModelTwo modelTwo = new ModelTwo();
//        modelTwo.setFst(longForModel);
//        modelTwo.setScd(strForModel);
//
//        variableUserActionContext.setValue("outModel", modelTwo);
    }

    @Override
    public Isolation getTransactionIsolationLevel() {
        return Isolation.DEFAULT;
    }
}
