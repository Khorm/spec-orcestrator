package com.petra.lib.remote.dto;

import com.petra.lib.context.enums.ExecutionStatus;

import java.util.UUID;

public class MessageDto {
    private UUID scenarioId;
    private Long receiverId;
    private String receiverVersion;
    private Long workflowId;
    private String transmittedValues;
    private Long senderId;
    private String senderVersion;
    private String senderServiceName;
    private ExecutionStatus status;

    public MessageDto(UUID scenarioId, Long receiverId, String receiverVersion, Long workflowId,
                      String transmittedValues, Long senderId, String senderVersion, String senderServiceName, ExecutionStatus status) {
        this.scenarioId = scenarioId;
        this.receiverId = receiverId;
        this.receiverVersion = receiverVersion;
        this.transmittedValues = transmittedValues;
        this.workflowId = workflowId;
        this.senderId = senderId;
        this.senderVersion = senderVersion;
        this.senderServiceName = senderServiceName;
        this.status = status;
    }

    public MessageDto() {
    }

    public UUID getScenarioId() {
        return scenarioId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getReceiverVersion() {
        return receiverVersion;
    }

    public String getTransmittedValues() {
        return transmittedValues;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderVersion() {
        return senderVersion;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public String getSenderServiceName() {
        return senderServiceName;
    }

    public void setTransmittedValues(String transmittedValues) {
        this.transmittedValues = transmittedValues;
    }

    public Long getWorkflowId() {
        return workflowId;
    }
}
