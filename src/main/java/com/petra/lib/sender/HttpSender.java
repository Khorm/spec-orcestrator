package com.petra.lib.sender;

import com.petra.lib.block.dto.BlockRequestDto;
import com.petra.lib.block.dto.BlockResponseDto;
import com.petra.lib.block.dto.SourceRequestDto;
import com.petra.lib.block.dto.SourceResponseDto;
import com.petra.lib.thread.ThreadController;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

class HttpSender implements Sender {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ThreadController threadController;

    HttpSender(ThreadController threadController) {
        this.threadController = threadController;
    }

    @Override
    public void sendToBlock(BlockRequestDto blockRequestDto, String serviceUrl, SenderCallback<Void> senderCallback) {
        threadController.executeUnlimitedPoolTask(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<BlockRequestDto> entity = new HttpEntity<>(blockRequestDto, headers);

            ResponseEntity<String> response
                    = restTemplate.exchange("http://" + serviceUrl + "/execute/", HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
//                    throw new RestClientException(serviceUrl + " connection error " + response.getStatusCode());
                threadController.executeLimitedPoolTask(() -> senderCallback.answer(null));

            } else {
                threadController.executeLimitedPoolTask(() -> senderCallback.error(null));
            }
        });

    }

    @Override
    public void answerFromBlock(BlockResponseDto blockResponseDto, String serviceUrl, SenderCallback<Void> senderCallback) {
        threadController.executeUnlimitedPoolTask(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<BlockResponseDto> entity = new HttpEntity<>(blockResponseDto, headers);

            ResponseEntity<String> response
                    = restTemplate.exchange("http://" + serviceUrl + "/answer/", HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
//                    throw new RestClientException(serviceUrl + " connection error " + response.getStatusCode());
                threadController.executeLimitedPoolTask(() -> senderCallback.answer(null));

            } else {
                threadController.executeLimitedPoolTask(() -> senderCallback.error(null));
            }
        });
    }

    @Override
    public void sendToSource(SourceRequestDto sourceRequestDto, String serviceUrl, SenderCallback<SourceResponseDto> senderCallback) {
        threadController.executeUnlimitedPoolTask(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SourceRequestDto> entity = new HttpEntity<>(sourceRequestDto, headers);
            ResponseEntity<SourceResponseDto> response
                    = restTemplate.exchange("http://" + serviceUrl + "/source_request/", HttpMethod.POST, entity, SourceResponseDto.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                threadController.executeLimitedPoolTask(() -> senderCallback.answer(response.getBody()));

            } else {
                threadController.executeLimitedPoolTask(() -> senderCallback.error(null));
            }

        });
    }
}
