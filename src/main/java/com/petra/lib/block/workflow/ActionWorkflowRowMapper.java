package com.petra.lib.block.workflow;

import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.workflow.model.ActionWorkflowHistory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ActionWorkflowRowMapper implements RowMapper<ActionWorkflowHistory> {

    @Override
    public ActionWorkflowHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID scenarioId = UUID.fromString(rs.getString("scenario_id"));
        Long consumerBlockId = rs.getLong("consumer_block_id");
        String consumerBlockVersion = rs.getString("consumer_block_version");
        Long producerId = rs.getLong("producer_block_id");
        String producerVersion = rs.getString("producer_block_version");
        ExecutionStatus status = ExecutionStatus.valueOf(rs.getString("status"));
        String consumerValues = rs.getString("consumer_values");


        return new ActionWorkflowHistory(scenarioId, consumerBlockId, consumerBlockVersion,
                producerId, producerVersion, status, consumerValues);
    }
}
