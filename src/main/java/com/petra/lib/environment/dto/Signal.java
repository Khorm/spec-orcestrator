package com.petra.lib.environment.dto;

import com.petra.lib.context.variables.VariablesContext;
import com.petra.lib.block.VersionBlockId;
import com.petra.lib.XXXXXXsignal.SignalId;
import com.petra.lib.environment.output.enums.AnswerType;
import com.petra.lib.environment.output.enums.RequestType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

/**
 * ������ ������������ ����� ������� � �������� �������.
 */
@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Signal {

    /**
     * ���� ��������
     */
    UUID scenarioId;

    /**
     * ���������� JSON ������������ � �������
     */
    VariablesContext variablesContext;

    /**
     * ���� �������
     */
    VersionBlockId signalId;

    /**
     * ��� �������
     */
    String signalName;

    /**
     * id ��������� �������
     */
    Long producerServiceId;
    /**
     * ��� ������� ��������� �������
     */
    String producerServiceName;

    /**
     * ���� ���������� ��� ���������� ������
     */
    VersionBlockId producerBlockId;

    /**
     * ���� ����������� ��� ��������� ������
     */
    VersionBlockId consumerBlockId;

    /**
     * ���� ��������� �������
     */
    Long consumerServiceId;

    /**
     * ��� ������� ������������ �������
     */
    String consumerServiceName;

    /**
     * ��� �������
     */
    RequestType requestType;
    AnswerType answerType;
}
