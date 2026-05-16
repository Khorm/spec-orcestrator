package com.petra.lib.variable.loader.impl.source;

import com.petra.lib.PetraException;
import com.petra.lib.constructor.model.SourceInputVariableModel;
import com.petra.lib.constructor.model.ValueLoaderDto;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.utils.JsonUtils;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.loader.impl.LoaderAbs;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class RemoteSource extends LoaderAbs {

    private final Sender sender;
    private final Collection<SourceInputVariableModel> sourceInputVariables;


    public RemoteSource(Sender sender,
                        ThreadController threadController,
                        Collection<SourceInputVariableModel> sourceInputVariables,
                        ValueLoaderDto valueModel,
                        List<Long> parents, List<ValueLoader> children) {
        super(threadController, valueModel, parents, children);
        this.sender = sender;
        this.sourceInputVariables = sourceInputVariables;
    }

    @Override
    protected ValueDto executeLoad(ValueContext context) {
        //парсинг переменных контекста в переменные соурса
        ValueContainer sourceContainer = ValueContainerFactory.getSimpleContainerByModels(List.of());
        for (SourceInputVariableModel sourceInputVariable : sourceInputVariables) {
            ValueDto toSourceVal = context.getValue(sourceInputVariable.getProducerVariable());
            String jsonSourceValue = toSourceVal.getExtractedJsonValue(sourceInputVariable.getExtractionString());

            ValueDto valueDto = new ValueDto(sourceInputVariable.getSourceVariable(), sourceInputVariable.getSourceValueName(),
                    sourceInputVariable.getSourceValueMultiplicity(), jsonSourceValue);
            sourceContainer.addValue(valueDto);
        }


        SourceRequestDto sourceRequestDto = new SourceRequestDto(
                context.getScenarioId(),
                getValueModel().getSourceId(),
                getValueModel().getSourceVersion(),
                sourceContainer
        );

        Optional<SourceResponseDto> answer = sender.sendToSource(sourceRequestDto, getValueModel().getSourceServicePath());
        return executeNext(answer, context);
    }

    private ValueDto executeNext(Optional<SourceResponseDto> answer, ValueContext context) {
        if (answer.isPresent()) {
            try {
                List<ValueDto> sourceAnswer = answer.get().getConsumerSourceResultValue();
                ValueDto resultValue;
                if (getValueModel().getExtractionString() != null && !getValueModel().getExtractionString().isBlank()) {
                    String json = JsonUtils.getExtractedJsonValue(getValueModel().getExtractionString(),
                            sourceAnswer.get(0).getJsonValue());
                    resultValue = new ValueDto(getVariableId(), getValueModel().getName(),
                            getValueModel().getMultiplicity(), json);
                } else {
                    resultValue = new ValueDto(getVariableId(), getValueModel().getName(),
                            getValueModel().getMultiplicity(), sourceAnswer.get(0).getJsonValue());
                }
                return resultValue;
            } catch (Exception e) {
                context.error(e);
                throw e;
            }
        } else {
            PetraException exception = new PetraException("Connection error with " + getValueModel().getSourceName());
            context.error(exception);
            throw exception;
        }
    }

}
