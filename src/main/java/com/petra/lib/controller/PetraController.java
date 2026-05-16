package com.petra.lib.controller;

import com.petra.lib.executor.RepeatException;
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
    UUID executeWorkflow(String workflowName, String version, Map<String, Object> params) throws RepeatException;
    void executeWorkflow(String workflowName, String version, Map<String, Object> params, UUID scenarioId) throws RepeatException;
    Result getResult(UUID scenarioId);




}
