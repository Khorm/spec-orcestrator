package com.petra.lib.variable.loader.impl.source;

import com.petra.lib.PetraException;
import com.petra.lib.remote.dto.SourceResponseDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import java.util.Optional;

class AnswerHandler {

    private final ValueContext context;
    private final ThreadController threadController;
    private final Long variableId;
    private final String variableName;
    private final Multiplicity multiplicity;
    private final String sourceName;
    private final SourceLoader thisLoader;
    private final Optional<SourceResponseDto> answer;

    AnswerHandler(ValueContext context, ThreadController threadController,
                          Long variableId, String variableName, Multiplicity multiplicity,
                          String sourceName, SourceLoader thisLoader, Optional<SourceResponseDto> answer) {
        this.context = context;
        this.threadController = threadController;
        this.variableId = variableId;
        this.variableName = variableName;
        this.multiplicity = multiplicity;
        this.sourceName = sourceName;
        this.thisLoader = thisLoader;
        this.answer = answer;
    }

    void execute() {
        if (answer.isPresent()) {
            threadController.executeLimitedPoolTask(() -> {
                try {
                    String sourceJsonAnswer = answer.get().getConsumerSourceValues();
                    Value contextValue = ValueFactory.createValue(variableId, variableName, multiplicity, sourceJsonAnswer);
                    context.setValue(contextValue, thisLoader);
                } catch (Exception e) {
                    context.error(e);
                }
            });
        } else {
            threadController.executeLimitedPoolTask(() -> {
                context.error(new PetraException("Connection error with " + sourceName));
            });
        }
    }


}
