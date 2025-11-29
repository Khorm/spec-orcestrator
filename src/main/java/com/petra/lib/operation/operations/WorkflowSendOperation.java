package com.petra.lib.operation.operations;

@Deprecated
class WorkflowSendOperation /*implements Operation<WorkflowContext>*/ {

//    private final Sender sender;
//    private final String currentServiceName;
//    private final ContextState CURRENT_STATE = ContextState.WORKFLOW_STARTING;
//
//
//    public WorkflowSendOperation(Sender sender, String currentServiceName) {
//        this.sender = sender;
//        this.currentServiceName = currentServiceName;
//    }
//
//    @Override
//    public void execute(WorkflowContext context) {
//        LocalProducer producer = context.getLocalProducer();
//        RemoteConsumer consumer = context.getRemoteConsumer();
//
//        MessageDto messageDto = new MessageDto(
//                context.getScenarioId(),
//                consumer.getIdentifier().getId(),
//                consumer.getIdentifier().getVersion(),
//                context.getWorkflowInputValues().getJson(),
//                producer.getIdentifier().getId(),
//                producer.getIdentifier().getVersion(),
//                currentServiceName,
//                null
//        );
//
//        sender.requestBlockExecution(messageDto, consumer.getServiceName(), new SenderCallback() {
//            @Override
//            public void answer(MessageResponse messageResponse) {
//                context.setState(null, CURRENT_STATE);
//            }
//
//            @Override
//            public void error(Exception e, MessageResponse messageResponse) {
//                context.error(e);
//            }
//        });
//
//    }
//
//    @Override
//    public ContextState getState() {
//        return CURRENT_STATE;
//    }
}
