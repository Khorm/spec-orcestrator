package com.petra.lib.remote;

import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.controller.PetraController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpListener {

    private final PetraController petraController;

    public HttpListener(PetraController petraController) {
        this.petraController = petraController;
    }

//    public HttpListener(Controller controller, RequestMappingHandlerMapping handlerMapping) throws NoSuchMethodException {
//        System.out.println("__________________________HttpListener created");
//        this.controller = controller;
//        Method blockRequestMethod = HttpListener.class.getMethod("blockRequest");
//        RequestMappingInfo mappingInfo = RequestMappingInfo
//                .paths("/execute_block")
//                .methods(RequestMethod.POST)
//                .produces("application/json")
//                .build();
//        handlerMapping.registerMapping(mappingInfo, controller, blockRequestMethod);
//
//        Method blockAnswerMethod = HttpListener.class.getMethod("blockAnswer");
//        mappingInfo = RequestMappingInfo
//                .paths("/answer_block")
//                .methods(RequestMethod.POST)
//                .produces("application/json")
//                .build();
//        handlerMapping.registerMapping(mappingInfo, controller, blockAnswerMethod);
//
//        Method sourceMethod = HttpListener.class.getMethod("executeSource");
//        mappingInfo = RequestMappingInfo
//                .paths("/source_request")
//                .methods(RequestMethod.POST)
//                .produces("application/json")
//                .build();
//        handlerMapping.registerMapping(mappingInfo, controller, sourceMethod);
//    }

    @RequestMapping(value = "/execute_block", method = RequestMethod.POST,
            produces = "application/json", consumes = "application/json")
    public ResponseEntity<HttpStatus> blockRequest(@RequestBody MessageDto blockRequestDto) {
        try {
            petraController.requestBlock(blockRequestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/answer_block", method = RequestMethod.POST,
            produces = "application/json", consumes = "application/json")
    public ResponseEntity<HttpStatus> blockAnswer(@RequestBody MessageDto answerDto) {
        try {
            petraController.blockAnswer(answerDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/source_request", method = RequestMethod.POST,
            produces = "application/json", consumes = "application/json")
    public ResponseEntity<MessageDto> executeSource(@RequestBody MessageDto sourceRequestDto) {
        try {
            MessageDto result = petraController.requestSource(sourceRequestDto);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
