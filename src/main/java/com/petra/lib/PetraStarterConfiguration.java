package com.petra.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.constructor.Constructor;
import com.petra.lib.constructor.PetraProperties;
import com.petra.lib.constructor.model.ConstructorModel;
import com.petra.lib.context.source.SourceUserHandler;
import com.petra.lib.controller.PetraController;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.remote.HttpListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@AutoConfiguration
@ConditionalOnClass(PetraController.class)
public class PetraStarterConfiguration {

    @Value("${spring.datasource.url:jdbc:postgresql://192.168.0.10:5432/postgres}")
    private String dbUrl;

    @Value("${spring.datasource.username:your_user}")
    private String username;

    @Value("${spring.datasource.password:your_password}")
    private String password;

    @Bean
    public PetraController petraController(JpaTransactionManager transactionManager, PetraProperties petraProperties, Map<String, UserActionHandler> userActionHandlerMap,
                                     Map<String, SourceUserHandler> sourceUserHandlerMap) throws IOException {
        System.out.println("PetraTestAware");
        Constructor constructor = new Constructor();
        ObjectMapper objectMapper = new ObjectMapper();
        ConstructorModel constructorModel = null;
        constructorModel = objectMapper.readValue(new File("src/main/resources/petra_config.json"), ConstructorModel.class);
        return constructor.construct(constructorModel, transactionManager, petraProperties,
                userActionHandlerMap, sourceUserHandlerMap);
    }

    @Bean(name = "petraHttpServer")
    public HttpListener httpListener(PetraController petraController){
        return new HttpListener(petraController);
    }



}
