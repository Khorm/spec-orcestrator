package com.petra.lib.variable.loader;

import com.petra.lib.PetraException;
import com.petra.lib.block.dto.SourceRequestDto;
import com.petra.lib.block.dto.SourceResponseDto;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.sender.Sender;
import com.petra.lib.sender.SenderCallback;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.VariableCallback;
import com.petra.lib.variable.VariableManager;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.value.Value;
import com.petra.lib.variable.value.ValueContainer;

import java.util.List;

class SourceLoader implements ValueLoader {

    private final List<ValueLoader> childValues;
    private final List<Long> parentValues;
    private final Sender sender;
    private final String producerServiceUrl;
    private final Identifier sourceId;
    private final String sourceUrl;
    private final long sourceVariableId;
    private final long currentVariableId;
    private final String currentVariableName;
    private final ThreadController threadController;
    private final VariableManager sourceVariableManager;

    SourceLoader(List<ValueLoader> childValues, List<Long> parentValues, Sender sender,
                 String producerServiceUrl, Identifier sourceId, String sourceUrl, long sourceVariableId,
                 long currentVariableId, String currentVariableName, ThreadController threadController, VariableManager sourceVariableManager) {
        this.childValues = childValues;
        this.parentValues = parentValues;
        this.sender = sender;
        this.producerServiceUrl = producerServiceUrl;
        this.sourceId = sourceId;
        this.sourceUrl = sourceUrl;
        this.sourceVariableId = sourceVariableId;
        this.currentVariableId = currentVariableId;
        this.currentVariableName = currentVariableName;
        this.threadController = threadController;
        this.sourceVariableManager = sourceVariableManager;
    }

    @Override
    public void load(ValueContext context) {
        if (!context.areValuesLoaded(parentValues)) return;
        boolean isSourceRegistered = context.addCallback(sourceId, new SenderCallback<>() {
            @Override
            public void answer(SourceResponseDto dto) {
                threadController.executeLimitedPoolTask(() -> {
                    try {
                        ValueContainer valueContainer = new ValueContainer(dto.getConsumerSourceValues());
                        Value sourceValue = valueContainer.getValue(sourceVariableId);
                        context.setValue(new Value(currentVariableId, sourceValue.getValue(), sourceValue.getMultiplicity(), currentVariableName));
                        for (ValueLoader valueLoader : childValues) {
                            valueLoader.load(context);
                        }
                    } catch (Exception e) {
                        context.error(e);
                    }
                });
            }

            @Override
            public void error(Exception e) {

            }

        });

        if (!isSourceRegistered) {
            String currentValuesJson = context.getJsonValues();
            sourceVariableManager.execute(currentValuesJson, context.getScenarioId(), new VariableCallback() {
                @Override
                public void loaded(ValueContainer valueContainer) {
                    SourceRequestDto sourceRequestDto = new SourceRequestDto(
                            context.getScenarioId(),
                            sourceId.getId(),
                            sourceId.getVersion(),
                            valueContainer.toJson(),
                            producerServiceUrl,
                            context.getCurrentBlockId(),
                            context.getCurrentVersion()
                    );

                    sender.sendToSource(sourceRequestDto, sourceUrl, new SenderCallback<>() {
                        @Override
                        public void answer(SourceResponseDto dto) {
                            threadController.executeLimitedPoolTask(() -> {
                                try {
                                    context.addAnswer(sourceId, dto);

                                    ValueContainer valueContainer = new ValueContainer(dto.getConsumerSourceValues());
                                    Value sourceValue = valueContainer.getValue(sourceVariableId);
                                    context.setValue(new Value(currentVariableId, sourceValue.getValue(), sourceValue.getMultiplicity(), currentVariableName));
                                    for (ValueLoader valueLoader : childValues) {
                                        valueLoader.load(context);
                                    }
                                } catch (Exception e) {
                                    context.error(e);
                                }

                            });
                        }

                        @Override
                        public void error(Exception e) {
                            context.error(new PetraException("Connection error with " + sourceUrl));
                        }
                    });
                }

                @Override
                public void error(Exception e) {
                    context.error(new PetraException("Values load error with " + sourceUrl));
                }
            });


        }
    }
}
