package com.petra.lib.remote;

import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;

import java.util.Optional;


public interface Sender {
    void requestBlockExecution(MessageDto messageDto, String serviceName, SenderCallback senderCallback);
    void answerAboutBlockExecution(MessageDto messageDto, String serviceName, SenderCallback senderCallback);
    Optional<SourceResponseDto> sendToSource(SourceRequestDto sourceRequestDto, String serviceUrl);
}
