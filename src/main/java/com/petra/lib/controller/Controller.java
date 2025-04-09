package com.petra.lib.controller;

import com.petra.lib.block.Block;
import com.petra.lib.block.dto.BlockRequestDto;
import com.petra.lib.block.dto.BlockResponseDto;
import com.petra.lib.block.dto.SourceRequestDto;
import com.petra.lib.block.dto.SourceResponseDto;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.source.Source;

import java.util.Map;

public class Controller {

    private final Map<Identifier, Block> blockMap;
    private final Map<Identifier, Source> sourceMap;

    public Controller(Map<Identifier, Block> blockMap, Map<Identifier, Source> sourceMap) {
        this.blockMap = blockMap;
        this.sourceMap = sourceMap;
    }

    public void executeBlockTask(BlockRequestDto blockRequestDto){
        Identifier identifier = new Identifier(blockRequestDto.getConsumerBlockId(), blockRequestDto.getConsumerBlockVersion());
        blockMap.get(identifier).execute(blockRequestDto);
    }

    public SourceResponseDto executeSourceTask(SourceRequestDto sourceRequestDto){
        return sourceMap.get(new Identifier(sourceRequestDto.getConsumerSourceId(), sourceRequestDto.getConsumerSourceVersion()))
                .execute(sourceRequestDto);
    }

    public void executeBlockAnswer(BlockResponseDto blockResponseDto){
        blockMap.get(new Identifier(blockResponseDto.getConsumerBlockId(), blockResponseDto.getConsumerBlockVersion())).answer(blockResponseDto);
    }
}
