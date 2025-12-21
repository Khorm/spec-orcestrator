package com.petra.lib.context.repo;


import com.petra.lib.context.model.ConsumerIdentifier;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.context.workflow.WorkflowContextState;
import com.petra.lib.transaction.TransactionManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Isolation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class WorkflowContextRepoImpl implements WorkflowContextRepo {

    private final TransactionManager transactionManager;

    public WorkflowContextRepoImpl(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Optional<WorkflowContextEntity> getWorkflowContext(ConsumerIdentifier consumerIdentifier, UUID scenarioId) {

        NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));

        String sql =
                " SELECT  scenario_id, workflow_id, workflow_version, " +
                        " consumer_id, consumer_version, state, result_values " +
                        " FROM workflow_context " +
                        " WHERE scenario_id = :scenarioId " +
                        " AND consumer_id = :consumerId " +
                        " AND consumer_version = :consumerVersion ";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("scenarioId", scenarioId)
                .addValue("consumerId", consumerIdentifier.getConsumerId().getId())
                .addValue("consumerVersion", consumerIdentifier.getConsumerId().getVersion());


        List<WorkflowContextEntity> result = namedParameterJdbcTemplate.query(sql, params,
                (RowMapper<WorkflowContextEntity>) new WorkflowContextEntityRowMapper());
        return result.stream().findFirst();

    }

    @Override
    public boolean insertContext(WorkflowContextEntity contextEntity) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));

        ConsumerIdentifier identifier = contextEntity.getConsumerIdentifier();
        Identifier consumerId = identifier.getConsumerId();
        Identifier workflowId = identifier.getWorkflowId();

        String sql = "INSERT INTO workflow_context (" +
                "scenario_id, " +
                "workflow_id, " +
                "workflow_version, " +
                "consumer_id, " +
                "consumer_version, " +
                "state, " +
                "result_values " +
                ") VALUES (" +
                ":scenarioId, " +
                ":workflowId, " +
                ":workflowVersion, " +
                ":consumerId, " +
                ":consumerVersion, " +
                ":state, " +
                ":resultValues " +
                ")";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("scenarioId", contextEntity.getScenarioId())
                .addValue("workflowId", workflowId.getId())
                .addValue("workflowVersion", workflowId.getVersion())
                .addValue("consumerId", consumerId.getId())
                .addValue("consumerVersion", consumerId.getVersion())
                .addValue("state", contextEntity.getWorkflowState().name())
                .addValue("resultValues", null);

        try {
            namedParameterJdbcTemplate.update(sql, params);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false; // Запись с таким ключом уже существует
        }
    }

    @Override
    public boolean updateContext(WorkflowContextEntity contextEntity) {
        return transactionManager.executeInTransaction(transaction -> {
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(
                    Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));

            ConsumerIdentifier identifier = contextEntity.getConsumerIdentifier();
            Identifier consumerId = identifier.getConsumerId();

            // Шаг 1: Выбираем строку с блокировкой
            String selectForUpdateSql = "SELECT state FROM workflow_context " +
                    "WHERE scenario_id = :scenarioId " +
                    "  AND consumer_id = :consumerId " +
                    "  AND consumer_version = :consumerVersion " +
                    "FOR UPDATE";

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("scenarioId", contextEntity.getScenarioId())
                    .addValue("consumerId", consumerId.getId())
                    .addValue("consumerVersion", consumerId.getVersion());

            try {
                String stateResult = template
                        .queryForObject(selectForUpdateSql, params, String.class);

                if (!WorkflowContextState.START.name().equals(stateResult)) {
                    return false; // Состояние не START — обновлять нельзя
                }

                // Шаг 2: Обновляем состояние и результат
                String updateSql = "UPDATE workflow_context " +
                        "SET state = :newState, " +
                        "    result_values = :resultValues " +
                        "WHERE scenario_id = :scenarioId " +
                        "  AND consumer_id = :consumerId " +
                        "  AND consumer_version = :consumerVersion";

                MapSqlParameterSource updateParams = new MapSqlParameterSource()
                        .addValue("scenarioId", contextEntity.getScenarioId())
                        .addValue("consumerId", consumerId.getId())
                        .addValue("consumerVersion", consumerId.getVersion())
                        .addValue("newState", contextEntity.getWorkflowState().name())
                        .addValue("resultValues", contextEntity.getResultValues().getModels());

                int updatedRows = template.update(updateSql, updateParams);
                return updatedRows > 0;

            } catch (Exception e) {
                return false; // Ошибки доступа, блокировки и т.п.
            }
        }, Isolation.SERIALIZABLE);
    }
}
