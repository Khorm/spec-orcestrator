package com.petra.lib.context.repo;


import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.context.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.model.Identifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LoadedContextRowMapper implements RowMapper<ContextEntity>, ResultSetExtractor<ContextEntity> {

    @Override
    public ContextEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return getLoadedContext(rs);
    }

    @Override
    public ContextEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        return getLoadedContext(rs);
    }

    private ContextEntity getLoadedContext(ResultSet rs) throws SQLException {
        UUID scenarioId = UUID.fromString(rs.getString("scenario_id"));

        Long blockId = rs.getLong("consume_id");
        String blockVersion = rs.getString("consumer_version");
        BlockType blockType = BlockType.valueOf(rs.getString("consumer_type"));

        ContextState contextState = ContextState.valueOf(rs.getString("context_state"));
        ExecutionStatus executionStatus = ExecutionStatus.valueOf(rs.getString("context_execution_status"));
        String values = rs.getString("context_values");

        Long producerId = rs.getLong("producer_id");
        String producerVersion = rs.getString("producer_version");
        String producerServiceUrl = rs.getString("producer_service_url");
        String producerValues = rs.getString("producer_values");




        return new ContextEntity(scenarioId,
                new Identifier(blockId, blockVersion),
                producerServiceUrl,
                new Identifier(producerId, producerVersion),
                blockType,
                values,
                contextState,
                executionStatus,
                producerValues
                );
    }
}
