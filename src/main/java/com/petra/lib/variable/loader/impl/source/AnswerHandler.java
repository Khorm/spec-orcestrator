package com.petra.lib.variable.loader.impl.source;

import com.petra.lib.remote.dto.SourceResponseDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.enums.Multiplicity;

import java.util.Optional;

@Deprecated
class AnswerHandler {

    private final ValueContext context;
    private final ThreadController threadController;
    private final Long variableId;
    private final String variableName;
    private final Multiplicity multiplicity;
    private final String sourceName;
    private final RemoteSource thisLoader;
    private final Optional<SourceResponseDto> answer;

    AnswerHandler(ValueContext context, ThreadController threadController,
                  Long variableId, String variableName, Multiplicity multiplicity,
                  String sourceName, RemoteSource thisLoader, Optional<SourceResponseDto> answer) {
        this.context = context;
        this.threadController = threadController;
        this.variableId = variableId;
        this.variableName = variableName;
        this.multiplicity = multiplicity;
        this.sourceName = sourceName;
        this.thisLoader = thisLoader;
        this.answer = answer;
    }

//    void execute() {
//        if (answer.isPresent()) {
//            threadController.executeLimitedPoolTask(() -> {
//                try {
//                    String sourceJsonAnswer = answer.get().getConsumerSourceValues();
//                    ValueModel valueModel = new ValueModel(variableId, variableName, multiplicity, sourceJsonAnswer);
//                    Value contextValue = ValueFactory.createValue(valueModel);
//                    context.registerLoadedValue(contextValue, thisLoader);
//                } catch (Exception e) {
//                    context.error(e);
//                }
//            });
//        } else {
//            threadController.executeLimitedPoolTask(() -> {
//                context.error(new PetraException("Connection error with " + sourceName));
//            });
//        }
//    }


}
