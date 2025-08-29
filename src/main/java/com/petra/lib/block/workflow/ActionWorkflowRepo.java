package com.petra.lib.block.workflow;

import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.block.workflow.model.ActionWorkflowHistory;
import com.petra.lib.transaction.TransactionManager;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class ActionWorkflowRepo {

    private final TransactionManager transactionManager;

    public ActionWorkflowRepo(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public boolean isHistoryAlreadyExist(UUID scenarioId, Identifier consumerId, Identifier producerId) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate
                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("scenarioId", scenarioId)
                .addValue("consumerBlockId", consumerId.getId())
                .addValue("consumerBlockVersion", consumerId.getVersion())
                .addValue("producerBlockId", producerId.getId())
                .addValue("producerBlockVersion", producerId.getVersion());

        return namedParameterJdbcTemplate.queryForRowSet("SELECT 1 FROM block_workflow_history " +
                        " WHERE scenario_id = :scenarioId AND consumer_block_id = :consumerBlockId AND " +
                        " consumer_block_version = :consumerBlockVersion AND" +
                        " producer_block_id = :producerBlockId AND producer_block_version = producerBlockVersion:",
                namedParameters).getInt(1) == 1;
    }

    public Collection<ActionWorkflowHistory> getModels(UUID scenarioId, Identifier workflowId){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate
                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("scenarioId", scenarioId)
                .addValue("workflowId", workflowId.getId())
                .addValue("workflowVersion", workflowId.getVersion());

        return namedParameterJdbcTemplate.query("SELECT * FROM block_workflow_history " +
                        " WHERE producer_block_id = :workflowId AND producer_block_version = :workflowVersion AND scenario_id = :scenarioId ",
                namedParameters, (new ActionWorkflowRowMapper()));
    }

    public void insertHistory(UUID scenarioId, Identifier consumerId, Identifier producerId,
                              String consumerValues, ExecutionStatus executionStatus) {
        String sql = "INSERT INTO block_workflow_history VALUES (:scenarioId, :consumerBlockId, :consumerBlockVersion, :producerBlockId, producerBlockVersion," +
                " :consumerValues, :consumerValues )";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate
                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));

        SqlParameterSource updateParams = new MapSqlParameterSource()
                .addValue("scenarioId", scenarioId)
                .addValue("consumerBlockId", consumerId.getId())
                .addValue("consumerBlockVersion", consumerId.getVersion())
                .addValue("producerBlockId", producerId.getId())
                .addValue("producerBlockVersion", producerId.getVersion())
                .addValue("consumerValues", consumerValues)
                .addValue("consumerValues", executionStatus.name());
        namedParameterJdbcTemplate.update(sql, updateParams);
    }
}
