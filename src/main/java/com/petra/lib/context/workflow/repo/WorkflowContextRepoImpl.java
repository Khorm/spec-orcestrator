package com.petra.lib.context.workflow.repo;


import com.petra.lib.context.workflow.WorkflowContextEntity;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class WorkflowContextRepoImpl implements WorkflowContextRepo {

//    private final TransactionManager transactionManager;
//
//    public WorkflowContextRepoImpl(TransactionManager transactionManager) {
//        this.transactionManager = transactionManager;
//    }


    @Override
    public boolean insertContext(WorkflowContextEntity context, Transaction transaction) {
//        return transactionManager.executeInTransaction(tx -> {
            NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                    new NamedParameterJdbcTemplate(Objects.requireNonNull(transaction.getDataSource()));

            Identifier workflowId = context.getWorkflowId();

            String sql = "INSERT INTO workflow_context (" +
                    "scenario_id, " +
                    "workflow_id, " +
                    "workflow_version, " +
                    "ctx_state, " +
                    "ctx_values "+
                    ") VALUES (" +
                    ":scenarioId, " +
                    ":workflowId, " +
                    ":workflowVersion, " +
                    ":state, " +
                    ":values "+
                    ")";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("scenarioId", context.getScenarioId())
                    .addValue("workflowId", workflowId.getId())
                    .addValue("workflowVersion", workflowId.getVersion())
                    .addValue("state", context.getWorkflowState().name())
                    .addValue("values", context.getContextValues().toDBJson());

            try {
                namedParameterJdbcTemplate.update(sql, params);
                return true;
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                return false; // Запись с таким ключом уже существует
            }
//        }, transaction);
    }


    @Override
    public Optional<WorkflowContextEntity> findContext(UUID scenarioId, Identifier workflowId, Transaction transaction, boolean isLock) {
//        return transactionManager.executeInTransaction(tx -> {
            NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                    new NamedParameterJdbcTemplate(Objects.requireNonNull(transaction.getDataSource()));

            String sql =
                    " SELECT * " +
                            " FROM workflow_context " +
                            " WHERE scenario_id = :scenarioId " +
                            " AND workflow_id = :workflowId " +
                            " AND workflow_version = :workflowVersion";
            if (isLock) {
                sql += " FOR UPDATE";
            }

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("scenarioId", scenarioId)
                    .addValue("workflowId", workflowId.getId())
                    .addValue("workflowVersion", workflowId.getVersion());

            List<WorkflowContextEntity> result = namedParameterJdbcTemplate.query(sql, params,
                    (RowMapper<WorkflowContextEntity>) new WorkflowContextEntityRowMapper());
            if (result.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(result.get(0));
            }
//        }, transaction);
    }

    @Override
    public Optional<WorkflowContextEntity> findFinishedContext(UUID scenarioId, Transaction tx) {

        NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                new NamedParameterJdbcTemplate(Objects.requireNonNull(
                        tx.getDataSource()));

        String sql =
                " SELECT * " +
                        " FROM workflow_context " +
                        " WHERE scenario_id = :scenarioId" ;


        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("scenarioId", scenarioId);

        List<WorkflowContextEntity> result = namedParameterJdbcTemplate.query(sql, params,
                (RowMapper<WorkflowContextEntity>) new WorkflowContextEntityRowMapper());
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }


    @Override
    public void save(WorkflowContextEntity entity, Transaction transaction) {
//        transactionManager.executeInTransaction((tx) -> {

            Identifier workflowId = entity.getWorkflowId();
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(
                    Objects.requireNonNull(transaction.getDataSource()));


            if (entity.isWorkflowStateChanged()) {
                String updateSql = "UPDATE workflow_context " +
                        " SET ctx_state = :newState " +
                        " WHERE scenario_id = :scenarioId " +
                        " AND workflow_id = :workflowId " +
                        " AND workflow_version = :workflowVersion";


                MapSqlParameterSource updateParams = new MapSqlParameterSource()
                        .addValue("scenarioId", entity.getScenarioId())
                        .addValue("workflowId", workflowId.getId())
                        .addValue("workflowVersion", workflowId.getVersion())
                        .addValue("newState", entity.getWorkflowState().name());

                template.update(updateSql, updateParams);
            }

            if (entity.isContextValuesUpdated()) {
                String updateSql = "UPDATE workflow_context " +
                        " SET ctx_values = :newScenValues " +
                        " WHERE scenario_id = :scenarioId " +
                        " AND workflow_id = :workflowId " +
                        " AND workflow_version = :workflowVersion";


                MapSqlParameterSource updateParams = new MapSqlParameterSource()
                        .addValue("scenarioId", entity.getScenarioId())
                        .addValue("workflowId", workflowId.getId())
                        .addValue("workflowVersion", workflowId.getVersion())
                        .addValue("newScenValues", entity.getContextValues().toDBJson());

                template.update(updateSql, updateParams);
            }

//        }, transaction);
    }
}
