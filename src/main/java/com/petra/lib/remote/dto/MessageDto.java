package com.petra.lib.remote.dto;

import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.variable.container.ValueModel;

import java.util.List;
import java.util.UUID;

public class MessageDto {
    private UUID scenarioId;
    private Long receiverId;
    private String receiverVersion;
    private List<ValueModel> transmittedValues;
    private Long senderId;
    private String senderVersion;
    private String senderServiceName;
    private ExecutionStatus status;

    /**
     *
     * @param scenarioId - айди текущего сценария
     * @param receiverId -
     * @param receiverVersion
     * @param transmittedValues
     * @param senderId
     * @param senderVersion
     * @param senderServiceName - имя сервиса, отправляющего сообщение
     * @param status - результат оработки сообщения
     */
    public MessageDto(UUID scenarioId, Long receiverId, String receiverVersion,
                      List<ValueModel> transmittedValues, Long senderId, String senderVersion, String senderServiceName, ExecutionStatus status) {
        this.scenarioId = scenarioId;
        this.receiverId = receiverId;
        this.receiverVersion = receiverVersion;
        this.transmittedValues = transmittedValues;
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

    public List<ValueModel> getTransmittedValues() {
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

    public void setTransmittedValues(List<ValueModel> transmittedValues) {
        this.transmittedValues = transmittedValues;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "scenarioId=" + scenarioId +
                ", receiverId=" + receiverId +
                ", receiverVersion='" + receiverVersion + '\'' +
                ", transmittedValues='" + transmittedValues + '\'' +
                ", senderId=" + senderId +
                ", senderVersion='" + senderVersion + '\'' +
                ", senderServiceName='" + senderServiceName + '\'' +
                ", status=" + status +
                '}';
    }
}
