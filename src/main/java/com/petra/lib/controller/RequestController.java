package com.petra.lib.controller;

import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;

public interface RequestController {
    MessageResponse requestBlock(MessageDto messageDto);
    SourceResponseDto requestSource(SourceRequestDto messageDto);
    void blockAnswer(MessageDto messageDto);
}
