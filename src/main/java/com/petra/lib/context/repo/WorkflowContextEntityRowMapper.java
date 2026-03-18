package com.petra.lib.context.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.context.model.ConsumerIdentifier;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.context.block.WorkflowContextState;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueModel;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class WorkflowContextEntityRowMapper implements RowMapper<WorkflowContextEntity>, ResultSetExtractor<WorkflowContextEntity> {
    @Override
    public WorkflowContextEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return getEntity(rs);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WorkflowContextEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        try {
            return getEntity(rs);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private WorkflowContextEntity getEntity(ResultSet rs) throws SQLException, JsonProcessingException {
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

//        String resultValuesJson = rs.getString("result_values");
//        ObjectMapper oj = new ObjectMapper();
//        List<ValueModel> resultValues = oj.readValue(resultValuesJson, oj.getTypeFactory().constructCollectionType(List.class, ValueModel.class));
//
//        if (resultValuesJson != null) {
//            entity.setResultValues(ValueContainerFactory.getImmutableContainer(resultValues));
//        }

        return entity;
    }
}
