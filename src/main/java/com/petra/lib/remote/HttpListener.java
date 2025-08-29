package com.petra.lib.remote;

import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.controller.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class HttpListener {

    private final Controller controller;

    public HttpListener(Controller controller, RequestMappingHandlerMapping handlerMapping) throws NoSuchMethodException {
        this.controller = controller;
        Method blockRequestMethod = HttpListener.class.getMethod("blockRequest");
        RequestMappingInfo mappingInfo = RequestMappingInfo
                .paths("/execute_block")
                .methods(RequestMethod.POST)
                .produces("application/json")
                .build();
        handlerMapping.registerMapping(mappingInfo, controller, blockRequestMethod);

        Method blockAnswerMethod = HttpListener.class.getMethod("blockAnswer");
        mappingInfo = RequestMappingInfo
                .paths("/answer_block")
                .methods(RequestMethod.POST)
                .produces("application/json")
                .build();
        handlerMapping.registerMapping(mappingInfo, controller, blockAnswerMethod);

        Method sourceMethod = HttpListener.class.getMethod("executeSource");
        mappingInfo = RequestMappingInfo
                .paths("/source_request")
                .methods(RequestMethod.POST)
                .produces("application/json")
                .build();
        handlerMapping.registerMapping(mappingInfo, controller, sourceMethod);
    }


    public ResponseEntity<HttpStatus> blockRequest(MessageDto blockRequestDto) {
        try {
            controller.requestBlock(blockRequestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<HttpStatus> blockAnswer(MessageDto answerDto) {
        try {
            controller.blockAnswer(answerDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<HttpStatus> executeSource(MessageDto sourceRequestDto) {
        try {
            controller.requestSource(sourceRequestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
