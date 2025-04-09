package com.petra.lib.block.action.repo;


import com.petra.lib.block.action.context.LoadedContext;
import com.petra.lib.block.enums.BlockState;
import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.enums.HistoryType;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.variable.value.ValueContainer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LoadedContextRowMapper implements RowMapper<LoadedContext>, ResultSetExtractor<LoadedContext> {
    @Override
    public LoadedContext mapRow(ResultSet rs, int rowNum) throws SQLException {
        return getLoadedContext(rs);
    }

    @Override
    public LoadedContext extractData(ResultSet rs) throws SQLException, DataAccessException {
        return getLoadedContext(rs);
    }

    private LoadedContext getLoadedContext(ResultSet rs) throws SQLException {
        Long blockId = rs.getLong("block_id");
        String blockVersion = rs.getString("block_version");
        UUID scenarioId = UUID.fromString(rs.getString("scenario_id"));
        BlockState blockState = BlockState.valueOf(rs.getString("block_state"));
        ExecutionStatus executionStatus = ExecutionStatus.valueOf(rs.getString("block_execution_status"));
        HistoryType historyType = HistoryType.valueOf(rs.getString("block_history_type"));
        Long producerId = rs.getLong("producer_id");
        String producerVersion = rs.getString("producer_version");
        String values = rs.getString("values");
        String producerServiceUrl = rs.getString("producer_service_url");


        return new LoadedContext(scenarioId, new Identifier(blockId, blockVersion),
                producerServiceUrl, new Identifier(producerId, producerVersion), values,
                historyType,blockState, executionStatus);
    }
}
