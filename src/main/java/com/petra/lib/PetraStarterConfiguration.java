package com.petra.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.actor.local.condition.ConditionUserHandler;
import com.petra.lib.constructor.Constructor;
import com.petra.lib.constructor.PetraProperties;
import com.petra.lib.constructor.model.ConstructorModel;
import com.petra.lib.actor.local.source.SourceUserHandler;
import com.petra.lib.controller.PetraController;
import com.petra.lib.controller.RequestController;
import com.petra.lib.actor.local.activity.UserActivityHandler;
import com.petra.lib.remote.HttpListener;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@AutoConfiguration
@ConditionalOnClass(PetraController.class)
@EnableConfigurationProperties(PetraProperties.class)
public class PetraStarterConfiguration {

    @Value("${spring.datasource.url:jdbc:postgresql://192.168.0.10:5432/postgres}")
    private String dbUrl;

    @Value("${spring.datasource.username:your_user}")
    private String username;

    @Value("${spring.datasource.password:your_password}")
    private String password;

    @Bean
    public PetraController petraController(JpaTransactionManager transactionManager, PetraProperties petraProperties, Map<String, UserActivityHandler> userActionHandlerMap,
                                           Map<String, SourceUserHandler> sourceUserHandlerMap, Map<String, ConditionUserHandler> conditionUserHandlerMap) throws IOException {
        System.out.println("PetraTestAware");
        Constructor constructor = new Constructor();
        ObjectMapper objectMapper = new ObjectMapper();
        ConstructorModel constructorModel = null;
        constructorModel = objectMapper.readValue(new File("src/main/resources/petra_config.json"), ConstructorModel.class);
        return constructor.construct(constructorModel, transactionManager, petraProperties,
                userActionHandlerMap, sourceUserHandlerMap, conditionUserHandlerMap);
    }

    @Bean(name = "petraHttpServer")
    public RouterFunction<ServerResponse> httpListener(RequestController petraController) {

        HttpListener httpListener = new HttpListener(petraController);
        return route()
                .POST("execute_block", request -> {
                    ResponseEntity<MessageResponse> r = httpListener.blockRequest(request.body(MessageDto.class));
                    if (r.getStatusCode() == HttpStatus.OK) {
                        return ServerResponse.ok().body(r.getBody());
                    }
                    return ServerResponse.status(r.getStatusCode()).build();
                })
                .POST("answer_block", request -> {
                    ResponseEntity<HttpStatus> r = httpListener.blockAnswer(request.body(MessageDto.class));
                    return ServerResponse.status(r.getStatusCode()).build();
                })
                .POST("source_request", request -> {
                    ResponseEntity<SourceResponseDto> r = httpListener.executeSource(request.body(SourceRequestDto.class));
                    if (r.getStatusCode() == HttpStatus.OK) {
                        return ServerResponse.ok().body(r.getBody());
                    }
                    return ServerResponse.status(r.getStatusCode()).build();
                })
                .build();
    }


}
