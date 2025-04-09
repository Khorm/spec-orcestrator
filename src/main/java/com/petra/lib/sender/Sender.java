package com.petra.lib.sender;

import com.petra.lib.block.dto.BlockRequestDto;
import com.petra.lib.block.dto.BlockResponseDto;
import com.petra.lib.block.dto.SourceRequestDto;
import com.petra.lib.block.dto.SourceResponseDto;


public interface Sender {
    void sendToBlock(BlockRequestDto blockRequestDto, String serviceUrl, SenderCallback<Void> senderCallback) ;
    void answerFromBlock(BlockResponseDto blockResponseDto, String serviceUrl, SenderCallback<Void> senderCallback) ;
    void sendToSource(SourceRequestDto sourceRequestDto, String serviceUrl, SenderCallback<SourceResponseDto> senderCallback);
}
