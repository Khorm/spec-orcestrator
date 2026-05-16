package com.petra.lib.z_user_package.source_handlers;


import com.petra.lib.actor.local.source.SourceUserContext;
import com.petra.lib.actor.local.source.SourceUserHandler;
import com.petra.lib.z_user_package.source_handlers.models.ModelOne;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service("GetModelOneService")
public class Source implements SourceUserHandler {
    @Override
    public void executeSource(SourceUserContext userContext) {
        Long longVal = userContext.getValue("long", Long.class);
        String stringValue = userContext.getValue("string", String.class);

        System.out.println("stringValue " +stringValue);

        Collection<String> collectionStringValues = new ArrayList<>();
        collectionStringValues.add(stringValue + " hello " + longVal);

        ModelOne modelOne = new ModelOne();
        modelOne.setInnerOne(longVal);
        modelOne.setInnerTwo(collectionStringValues);

        System.out.println("GetModelOneService " + modelOne);
        userContext.setValue("model", modelOne);
    }
}
