package com.petra.lib.context.repo;

import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.context.model.ConsumerIdentifier;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.context.workflow.WorkflowContextState;
import com.petra.lib.variable.container.ValueContainerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WorkflowContextEntityRowMapper implements RowMapper<WorkflowContextEntity>, ResultSetExtractor<WorkflowContextEntity> {
    @Override
    public WorkflowContextEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
       return getEntity(rs);
    }

    @Override
    public WorkflowContextEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        return getEntity(rs);
    }

    private WorkflowContextEntity getEntity(ResultSet rs) throws SQLException {
        Identifier consumerId = new Identifier(
                rs.getLong("consumer_id"),
                rs.getString("consumer_version")
        );

        Identifier workflowId = new Identifier(
                rs.getLong("workflow_id"),
                rs.getString("workflow_version")
        );

        ConsumerIdentifier consumerIdentifier = new ConsumerIdentifier(consumerId, workflowId);

        WorkflowContextEntity entity = new WorkflowContextEntity(consumerIdentifier, UUID.fromString(rs.getString("scenario_id")));

        String stateStr = rs.getString("state");
        if (stateStr != null) {
            entity.setWorkflowState(WorkflowContextState.valueOf(stateStr));
        }

        String resultValuesJson = rs.getString("result_values");
        if (resultValuesJson != null) {
            entity.setResultValues(ValueContainerFactory.getImmutableContainer(resultValuesJson));
        }

        return entity;
    }
}
