package com.petra.lib.variable.loader.impl.source;

import com.petra.lib.PetraException;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.constructor.model.SourceInputVariableModel;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.loader.impl.LoaderAbs;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import java.util.List;
import java.util.Optional;

public final class SourceLoader extends LoaderAbs {
    private final Sender sender;
    private final Identifier sourceId;
    private final String sourceName;
    private final Long currentVariableId;
    private final String currentVariableName;
    private final ThreadController threadController;
    private final Multiplicity currentMultiplicity;
    private final List<SourceInputVariable> sourceInputVariables;


    public SourceLoader(List<ValueLoader> childValues, List<Long> parentValues, Sender sender,
                 Identifier sourceId, String sourceName,
                 Long currentVariableId, String currentVariableName, ThreadController threadController,
                 Multiplicity currentMultiplicity, List<SourceInputVariable> sourceInputVariables) {
        super(childValues, parentValues, threadController);
        this.sender = sender;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.sourceInputVariables = sourceInputVariables;
        this.currentVariableId = currentVariableId;
        this.currentVariableName = currentVariableName;
        this.currentMultiplicity = currentMultiplicity;
        this.threadController = threadController;
    }

    @Override
    protected void executeLoad(ValueContext context) {
        //парсинг переменных контекста в переменные соурса
        ValueContainer sourceContainer = ValueContainerFactory.getSimpleContainer();
        for (SourceInputVariable sourceInputVariable : sourceInputVariables) {
            Value toSourceVal = context.getValue(sourceInputVariable.getCurrentBlockVariable());
            String jsonSourceValue = toSourceVal.getExtractedJsonValue(sourceInputVariable.getExtractionString());
            Value sourceInputValue = ValueFactory.createValue(sourceInputVariable.getSourceVariable(), sourceInputVariable.getSourceValueName(),
                    sourceInputVariable.getSourceValueMultiplicity(), jsonSourceValue);
            sourceContainer.setValue(sourceInputValue);
        }


        SourceRequestDto sourceRequestDto = new SourceRequestDto(
                context.getScenarioId(),
                sourceId.getId(),
                sourceId.getVersion(),
                sourceContainer
        );


        Optional<SourceResponseDto> answer = sender.sendToSource(sourceRequestDto, sourceName);
        AnswerHandler answerHandler = new AnswerHandler(context, threadController,
                currentVariableId, currentVariableName, currentMultiplicity, sourceName, this, answer);
        answerHandler.execute();

    }

}
