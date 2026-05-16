package com.petra.lib.context.workflow.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.context.enums.WorkflowContextState;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueDto;
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
        String stateStr = rs.getString("ctx_state");

        String resultValuesJson = rs.getString("ctx_values");
        ObjectMapper oj = new ObjectMapper();
        ValueContainer valueContainer;

        if (resultValuesJson != null) {
            List<ValueDto> resultValues = oj.readValue(resultValuesJson,
                    oj.getTypeFactory().constructCollectionType(List.class, ValueDto.class));
            valueContainer = ValueContainerFactory.getSimpleContainerByDtos(resultValues);
        }else {
            valueContainer = ValueContainerFactory.getSimpleContainerByModels(List.of());
        }

        return new WorkflowContextEntity(workflowId,
                UUID.fromString(rs.getString("scenario_id")),
                valueContainer,
                WorkflowContextState.valueOf(stateStr)
        );
    }
}
