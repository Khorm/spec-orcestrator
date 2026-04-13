package com.petra.lib.context.workflow.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
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

        Identifier workflowId = new Identifier(
                rs.getLong("workflow_id"),
                rs.getString("workflow_version")
        );
        String stateStr = rs.getString("state");

        String resultValuesJson = rs.getString("scenario_values");
        ObjectMapper oj = new ObjectMapper();
        List<ValueModel> resultValues = oj.readValue(resultValuesJson, oj.getTypeFactory().constructCollectionType(List.class, ValueModel.class));
        ValueContainer valueContainer;

        if (resultValuesJson != null) {
            valueContainer = ValueContainerFactory.getSimpleContainer(resultValues);
        }else {
            valueContainer = ValueContainerFactory.getSimpleContainer();
        }

        return new WorkflowContextEntity(workflowId,
                UUID.fromString(rs.getString("scenario_id")),
                valueContainer,
                WorkflowContextState.valueOf(stateStr)
        );
    }
}
