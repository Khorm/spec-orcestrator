package com.petra.lib.listener;

import com.petra.lib.block.dto.BlockRequestDto;
import com.petra.lib.block.dto.SourceRequestDto;
import com.petra.lib.controller.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpListener {

    Controller controller;

    public ResponseEntity<HttpStatus> executeBlock(BlockRequestDto blockRequestDto) {
        try {
            controller.executeBlockTask(blockRequestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<HttpStatus> executeSource(SourceRequestDto sourceRequestDto) {
        try {
            controller.executeSourceTask(sourceRequestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @PostMapping(path = "execute")
//    public BlockRequestResult execute(BlockRequestDto blockRequestDto) {
//        return inputController.execute(blockRequestDto);
//    }
//
//    @PostMapping(path = "approve")
//    public BlockResponseResult approve(BlockResponseDto signal) {
//        return inputController.handleAnswer(signal);
//    }
//
//
//    @PostMapping(path = "source_request")
//    public BlockRequestResult getSource(SourceRequestDto sourceRequestDto) {
//        return inputController.handleSource(sourceRequestDto);
//    }
//
//    @PostMapping(path = "service_response")
//    public BlockResponseResult sourceAnswer(SourceResponseDto sourceResponseDto){
//        return inputController.sourceAnswer(sourceResponseDto);
//    }
}
