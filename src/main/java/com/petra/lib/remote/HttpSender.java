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
    public void answerAboutBlockExecution(MessageDto messageDto, String serviceName, SenderCallback senderCallback) {
        sendToBlock(messageDto, serviceName, senderCallback, "answer_block");
    }

    private void sendToBlock(MessageDto messageDto, String serviceName, SenderCallback senderCallback, String command) {
        threadController.executeUnlimitedPoolTask(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<MessageDto> entity = new HttpEntity<>(messageDto, headers);

                ResponseEntity<String> response
                        = restTemplate.exchange("http://" + serviceName + "/" + command ,
                        HttpMethod.POST, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    threadController.executeLimitedPoolTask(() -> senderCallback.answer(null));

                } else {
                    threadController.executeLimitedPoolTask(()
                            -> senderCallback.error(null, new MessageResponse(response.getStatusCode())));
                }
            } catch (Exception e) {
                threadController.executeLimitedPoolTask(()
                        -> senderCallback.error(e, null));
            }
        });
    }

//    @Override
//    public void answerFromBlock(MessageDto messageDto, String serviceUrl, SenderCallback<Void> senderCallback) {
//        threadController.executeUnlimitedPoolTask(() -> {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<MessageDto> entity = new HttpEntity<>(messageDto, headers);
//
//            ResponseEntity<String> response
//                    = restTemplate.exchange("http://" + serviceUrl + "/answer/", HttpMethod.POST, entity, String.class);
//
//            if (response.getStatusCode() == HttpStatus.OK) {
////                    throw new RestClientException(serviceUrl + " connection error " + response.getStatusCode());
//                threadController.executeLimitedPoolTask(() -> senderCallback.answer(null));
//
//            } else {
//                threadController.executeLimitedPoolTask(() -> senderCallback.error(null));
//            }
//        });
//    }

    @Override
    public Optional<SourceResponseDto> sendToSource(SourceRequestDto sourceRequestDto, String serviceUrl) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SourceRequestDto> entity = new HttpEntity<>(sourceRequestDto, headers);
        ResponseEntity<SourceResponseDto> response
                = restTemplate.exchange("http://" + serviceUrl + "/source_request", HttpMethod.POST, entity, SourceResponseDto.class);
        if (response.getStatusCode() == HttpStatus.OK) {
//                threadController.executeLimitedPoolTask(() -> senderCallback.answer(response.getBody()));
            return Optional.ofNullable(response.getBody());

        } else {
//            threadController.executeLimitedPoolTask(() -> senderCallback.error(null));
            return Optional.empty();
        }
    }
}
