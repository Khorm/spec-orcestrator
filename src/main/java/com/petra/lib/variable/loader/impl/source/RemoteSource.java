package com.petra.lib.variable.loader.impl.source;

import com.petra.lib.PetraException;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.utils.JsonUtils;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueModel;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.enums.Multiplicity;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.loader.impl.LoaderAbs;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import java.util.List;
import java.util.Optional;

public final class RemoteSource extends LoaderAbs {
    private final Sender sender;

    private final Identifier sourceId;
    private final String sourceName;
//    private final Multiplicity sourceMultiplicity;
    private final List<SourceInputVariable> sourceInputVariables;

    private final Long currentVariableId;
    private final String currentVariableName;
    private final Multiplicity currentMultiplicity;
    private final String extractionString;


    public RemoteSource(List<ValueLoader> childValues, List<Long> parentValues, Sender sender,
                        Identifier sourceId, String sourceName,
                        Long currentVariableId, String currentVariableName, ThreadController threadController,
                        Multiplicity currentMultiplicity, List<SourceInputVariable> sourceInputVariables, String extractionString) {
        super(childValues, parentValues, threadController);
        this.sender = sender;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
//        this.sourceMultiplicity = sourceMultiplicity;
        this.sourceInputVariables = sourceInputVariables;
        this.currentVariableId = currentVariableId;
        this.currentVariableName = currentVariableName;
        this.currentMultiplicity = currentMultiplicity;
        this.extractionString = extractionString;
    }

    @Override
    protected void executeLoad(ValueContext context) {
        //парсинг переменных контекста в переменные соурса
        ValueContainer sourceContainer = ValueContainerFactory.getSimpleContainer();
        for (SourceInputVariable sourceInputVariable : sourceInputVariables) {
            Value toSourceVal = context.getValue(sourceInputVariable.getCurrentBlockVariable());
            String jsonSourceValue = toSourceVal.getExtractedJsonValue(sourceInputVariable.getExtractionString());

            ValueModel valueModel = new ValueModel(sourceInputVariable.getSourceVariable(), sourceInputVariable.getSourceValueName(),
                    sourceInputVariable.getSourceValueMultiplicity(), jsonSourceValue);
            Value sourceInputValue = ValueFactory.createValue(valueModel);
            sourceContainer.setValue(sourceInputValue);
        }


        SourceRequestDto sourceRequestDto = new SourceRequestDto(
                context.getScenarioId(),
                sourceId.getId(),
                sourceId.getVersion(),
                sourceContainer
        );

        Optional<SourceResponseDto> answer = sender.sendToSource(sourceRequestDto, sourceName);
        executeNext(answer, context);
    }

    private void executeNext(Optional<SourceResponseDto> answer, ValueContext context) {
        if (answer.isPresent()) {
            try {
                String sourceJsonAnswer = answer.get().getConsumerSourceValues();
                Value resultValue;
                if (extractionString != null) {
                    String json = JsonUtils.getExtractedJsonValue(sourceJsonAnswer, extractionString);
                    ValueModel valueModel = new ValueModel(currentVariableId, currentVariableName, currentMultiplicity, json);
                    resultValue = ValueFactory.createValue(valueModel);
                } else {
                    ValueModel valueModel = new ValueModel(currentVariableId, currentVariableName, currentMultiplicity, sourceJsonAnswer);
                    resultValue = ValueFactory.createValue(valueModel);
                }
                context.setValue(resultValue, this);
            } catch (Exception e) {
                context.error(e);
            }
        } else {
            context.error(new PetraException("Connection error with " + sourceName));
        }
    }

}
