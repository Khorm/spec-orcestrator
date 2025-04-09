package com.petra.lib.block;

import com.petra.lib.block.dto.BlockRequestDto;
import com.petra.lib.block.dto.BlockResponseDto;

public interface Block {
    void execute(BlockRequestDto blockRequestDto);
    void start();

    void answer(BlockResponseDto blockResponseDto);

}
