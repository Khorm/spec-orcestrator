package com.petra.lib.remote;

import com.petra.lib.controller.PetraController;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.controller.PetraControllerImpl;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpListener {

    private final PetraController petraControllerImpl;

    public HttpListener(PetraController petraControllerImpl) {
        this.petraControllerImpl = petraControllerImpl;
    }


    @RequestMapping(value = "/execute_block", method = RequestMethod.POST,
            produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> blockRequest(@RequestBody MessageDto blockRequestDto) {
        try {
            boolean isRepeat = petraControllerImpl.requestBlock(blockRequestDto);
            if (!isRepeat){
                return ResponseEntity.ok("OK");
            }else {
                return ResponseEntity.ok("REPEAT");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/answer_block", method = RequestMethod.POST,
            produces = "application/json", consumes = "application/json")
    public ResponseEntity<HttpStatus> blockAnswer(@RequestBody MessageDto answerDto) {
        try {
            petraControllerImpl.blockAnswer(answerDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/source_request", method = RequestMethod.POST,
            produces = "application/json", consumes = "application/json")
    public ResponseEntity<SourceResponseDto> executeSource(@RequestBody SourceRequestDto sourceRequestDto) {
        try {
            SourceResponseDto result = petraControllerImpl.requestSource(sourceRequestDto);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
