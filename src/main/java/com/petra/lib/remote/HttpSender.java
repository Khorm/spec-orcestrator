package com.petra.lib.remote;

import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;
import com.petra.lib.thread.ThreadController;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class HttpSender implements Sender {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ThreadController threadController;

    public HttpSender(ThreadController threadController) {
        this.threadController = threadController;
    }

    @Override
    public void requestBlockExecution(MessageDto messageDto, String serviceName, SenderCallback senderCallback) {
        sendToBlock(messageDto, serviceName, senderCallback, "execute_block");
    }

    @Override
    public void answerBlockExecution(MessageDto messageDto, String serviceName, SenderCallback senderCallback) {
        sendToBlock(messageDto, serviceName, senderCallback, "answer_block");
    }

    private void sendToBlock(MessageDto messageDto, String serviceName, SenderCallback senderCallback, String command) {
        threadController.executeUnlimitedPoolTask(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<MessageDto> entity = new HttpEntity<>(messageDto, headers);

                ResponseEntity<String> response
                        = restTemplate.exchange("http://" + serviceName + ":8080/" + command ,
                        HttpMethod.POST, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    threadController.executeUnlimitedPoolTask(()
                            -> senderCallback.answer(new MessageResponse(response.getStatusCode(), response.getBody())));

                } else {
                    threadController.executeUnlimitedPoolTask(()
                            -> senderCallback.error(null, new MessageResponse(response.getStatusCode(), null)));
                }
            } catch (Exception e) {
                threadController.executeUnlimitedPoolTask(()
                        -> senderCallback.error(e, null));
            }
        });
    }


    @Override
    public Optional<SourceResponseDto> sendToSource(SourceRequestDto sourceRequestDto, String serviceUrl) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SourceRequestDto> entity = new HttpEntity<>(sourceRequestDto, headers);
        ResponseEntity<SourceResponseDto> response
                = restTemplate.exchange("http://" + serviceUrl + ":8080/source_request", HttpMethod.POST, entity, SourceResponseDto.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Optional.ofNullable(response.getBody());

        } else {
            return Optional.empty();
        }
    }
}
