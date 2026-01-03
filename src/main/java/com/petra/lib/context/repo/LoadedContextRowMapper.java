package com.petra.lib.context.repo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.variable.container.ValueModel;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class LoadedContextRowMapper implements RowMapper<ContextEntity>, ResultSetExtractor<ContextEntity> {

    @Override
    public ContextEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return getLoadedContext(rs);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ContextEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        try {
            return getLoadedContext(rs);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ContextEntity getLoadedContext(ResultSet rs) throws SQLException, JsonProcessingException {
        UUID scenarioId = UUID.fromString(rs.getString("scenario_id"));

        Long consumerId = rs.getLong("consumer_id");
        String consumerVersion = rs.getString("consumer_version");

        BlockType blockType = BlockType.valueOf(rs.getString("consumer_type"));

        String contextStateStr = rs.getString("context_state");
        ContextState contextState = contextStateStr != null ? ContextState.valueOf(contextStateStr) : null;

        String executionStatusStr = rs.getString("context_execution_status");
        ExecutionStatus executionStatus = executionStatusStr != null ? ExecutionStatus.valueOf(executionStatusStr) : null;

        String valuesStr = rs.getString("context_values");

        Long producerId = rs.getLong("producer_id");
        String producerVersion = rs.getString("producer_version");
        String producerServiceName = rs.getString("producer_service_name");
        String producerValuesStr = rs.getString("producer_values");
        ObjectMapper oj = new ObjectMapper();
        List<ValueModel> producerValues = oj.readValue(producerValuesStr, oj.getTypeFactory().constructCollectionType(List.class, ValueModel.class));
        List<ValueModel> outValues = oj.readValue(valuesStr, oj.getTypeFactory().constructCollectionType(List.class, ValueModel.class));

        return new ContextEntity(
                scenarioId,
                new Identifier(consumerId, consumerVersion),
                producerServiceName,
                new Identifier(producerId, producerVersion),
                contextState,
                executionStatus,
                producerValues,
                blockType,
                outValues
        );
    }
}
