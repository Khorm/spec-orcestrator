package com.petra.lib.remote;

import com.petra.lib.controller.RequestController;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Log4j2
public class HttpListener {

    private final RequestController petraControllerImpl;

    public HttpListener(RequestController petraControllerImpl) {
        this.petraControllerImpl = petraControllerImpl;
    }


    public ResponseEntity<MessageResponse> blockRequest(@RequestBody MessageDto blockRequestDto) {
        try {
            MessageResponse response = petraControllerImpl.requestBlock(blockRequestDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e);
            MessageResponse response = new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR", null);
            return ResponseEntity.ok(response);
        }
    }


    public ResponseEntity<HttpStatus> blockAnswer(@RequestBody MessageDto answerDto) {
        try {
            petraControllerImpl.blockAnswer(answerDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<SourceResponseDto> executeSource(@RequestBody SourceRequestDto sourceRequestDto) {
        try {
            SourceResponseDto result = petraControllerImpl.requestSource(sourceRequestDto);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
