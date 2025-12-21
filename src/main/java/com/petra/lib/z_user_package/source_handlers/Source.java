package com.petra.lib.z_user_package.source_handlers;


import com.petra.lib.context.source.SourceUserContext;
import com.petra.lib.context.source.SourceUserHandler;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service("TestSource")
public class Source implements SourceUserHandler {
    @Override
    public void executeSource(SourceUserContext userContext) {
        System.out.println("TestSource");
        System.out.println("SOURCE USING InVarNumber " + userContext.getValue("InVarNumber", Long.class));
        List<String> list = userContext.getListValue("SecondInVarList", String.class);
        for (String s : list) {
            System.out.println("SOURCE USING SecondInVarList " + s);
        }

        userContext.setValue("OutVat", "Hello from service TestSource " + list.get(0));
    }
}
