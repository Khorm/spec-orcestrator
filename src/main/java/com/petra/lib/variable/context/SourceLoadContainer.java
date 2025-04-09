package com.petra.lib.variable.context;

import com.petra.lib.block.dto.SourceResponseDto;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.sender.SenderCallback;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class SourceLoadContainer {
    private final Map<Identifier, SourceResponseDto> sourceAnswers = new HashMap<>();
    private final Map<Identifier, List<SenderCallback<SourceResponseDto>>> sourceCallbacks = new HashMap<>();


    void addAnswer(Identifier sourceId, SourceResponseDto answer) {
        sourceAnswers.put(sourceId, answer);
        sourceCallbacks.get(sourceId).forEach(sourceCallback -> sourceCallback.answer(answer));
    }


    void addCallback(Identifier sourceId, SenderCallback<SourceResponseDto> senderCallback) {
        if (!sourceCallbacks.containsKey(sourceId)) {
            sourceCallbacks.put(sourceId, new ArrayList<>());
        }

        sourceCallbacks.get(sourceId).add(senderCallback);
        if (sourceAnswers.containsKey(sourceId)) {
            senderCallback.answer(sourceAnswers.get(sourceId));
        }
    }

    void registerSourceRequest(Identifier sourceId) {
        sourceAnswers.put(sourceId, null);
    }

    boolean isSourceRequested(Identifier sourceId) {
        return sourceAnswers.containsKey(sourceId);
    }
}
