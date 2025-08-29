package com.petra.lib.context.workflow.operations;

import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.model.LocalProducer;
import com.petra.lib.context.model.RemoteConsumer;
import com.petra.lib.context.operation.Operation;
import com.petra.lib.context.workflow.WorkflowContext;
import com.petra.lib.remote.MessageResponse;
import com.petra.lib.remote.Sender;
import com.petra.lib.remote.SenderCallback;

public class WorkflowSendOperation implements Operation<WorkflowContext> {

    private final Sender sender;
    private final String currentServiceName;
    private final ContextState CURRENT_STATE = ContextState.STARTED;


    public WorkflowSendOperation(Sender sender, String currentServiceName) {
        this.sender = sender;
        this.currentServiceName = currentServiceName;
    }

    @Override
    public void execute(WorkflowContext context) {
        LocalProducer producer = context.getLocalProducer();
        RemoteConsumer consumer = context.getRemoteConsumer();

        MessageDto messageDto = new MessageDto(
                context.getScenarioId(),
                consumer.getIdentifier().getId(),
                consumer.getIdentifier().getVersion(),
                context.getWorkflowInputValues().getJson(),
                producer.getIdentifier().getId(),
                producer.getIdentifier().getVersion(),
                currentServiceName,
                null
        );

        sender.requestBlockExecution(messageDto, consumer.getServiceName(), new SenderCallback() {
            @Override
            public void answer(MessageResponse messageResponse) {
                context.setState(null, CURRENT_STATE);
            }

            @Override
            public void error(Exception e, MessageResponse messageResponse) {
                context.error(e);
            }
        });

    }

    @Override
    public ContextState getState() {
        return CURRENT_STATE;
    }
}
