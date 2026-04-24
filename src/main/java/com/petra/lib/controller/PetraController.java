package com.petra.lib.controller;

import com.petra.lib.remote.dto.MessageDto;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.remote.dto.SourceResponseDto;

import java.util.Map;
import java.util.UUID;

public interface PetraController {

    /**
     * Execute a Workflow
     * @param workflowName workflow name
     * @param version workflow version
     * @param params workflow parameters
     */
    void executeWorkflow(String workflowName, String version, Map<String, Object> params);
    void executeWorkflow(String workflowName, String version, Map<String, Object> params, UUID scenarioId);
    Result getResult(UUID scenarioId);

    boolean requestBlock(MessageDto messageDto);
    SourceResponseDto requestSource(SourceRequestDto messageDto);
    void blockAnswer(MessageDto messageDto);


}
