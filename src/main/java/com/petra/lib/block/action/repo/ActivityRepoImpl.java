package com.petra.lib.block.action.repo;

import com.petra.lib.block.action.context.ActionContext;
import com.petra.lib.block.action.context.LoadedContext;
import com.petra.lib.block.enums.BlockState;
import com.petra.lib.block.enums.ExecutionStatus;
import com.petra.lib.block.enums.HistoryType;
import com.petra.lib.block.model.Identifier;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.value.ValueContainer;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ActivityRepoImpl implements ActionRepo {

    private final TransactionManager transactionManager;

    public ActivityRepoImpl(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

//    public boolean updateBlockHistoryVariables(UUID scenario, Identifier blockId,
//                                               ValueContainer actionVariables) {
//
//        String updateSql = "UPDATE block_history SET block_values = :blockVariables " +
//                " where block_id = :blockId AND block_version = :blockVersion AND scenario_id = :scenarioId;";
//        NamedParameterJdbcTemplate namedParameterJdbcTemplate
//                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));
//        SqlParameterSource updateParams = new MapSqlParameterSource()
//                .addValue("blockVariables", actionVariables.toJson())
//                .addValue("blockId", blockId.getId())
//                .addValue("blockVersion", blockId.getVersion())
//                .addValue("scenarioId", scenario);
//        return namedParameterJdbcTemplate.update(updateSql, updateParams) == 1;
//
//
//    }

    @Override
    public BlockState findCurrentState(UUID scenarioId, Identifier blockId) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate
                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("blockId", blockId.getId())
                .addValue("blockVersion", blockId.getVersion())
                .addValue("scenario_id", scenarioId);

        return BlockState.valueOf(namedParameterJdbcTemplate.queryForRowSet("SELECT block_state FROM block_history " +
                        " WHERE block_id = :blockId AND block_version = :blockVersion AND" +
                        " scenario_id = :scenarioId ",
                namedParameters).getString("block_state"));
    }

    public boolean updateBlockState(UUID scenario, Identifier blockId, BlockState blockState) {
        String updateSql = "UPDATE block_history SET block_state = :status " +
                " where block_id = :blockId AND block_version = :blockVersion AND scenario_id = :scenarioId;";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate
                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));
        SqlParameterSource updateParams = new MapSqlParameterSource()
                .addValue("blockId", blockId.getId())
                .addValue("blockVersion", blockId.getVersion())
                .addValue("scenarioId", scenario)
                .addValue("status", blockState.name());
        return namedParameterJdbcTemplate.update(updateSql, updateParams) == 1;
    }

    @Override
    public void updateExecutionStatus(UUID scenario, Identifier blockId, ExecutionStatus executionStatus) {
        String updateSql = "UPDATE block_history SET block_execution_status = :status " +
                " where block_id = :blockId AND block_version = :blockVersion AND scenario_id = :scenarioId;";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate
                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));
        SqlParameterSource updateParams = new MapSqlParameterSource()
                .addValue("blockId", blockId.getId())
                .addValue("blockVersion", blockId.getVersion())
                .addValue("scenarioId", scenario)
                .addValue("status", executionStatus.name());
        namedParameterJdbcTemplate.update(updateSql, updateParams);
    }


    @Override
    public Optional<LoadedContext> findContext(UUID scenarioId, Identifier blockId) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate
                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("blockId", blockId.getId())
                .addValue("blockVersion", blockId.getVersion())
                .addValue("scenario_id", scenarioId);

        return Optional.ofNullable(namedParameterJdbcTemplate.query("SELECT * FROM block_history " +
                        " WHERE block_id = :blockId AND block_version = :blockVersion AND" +
                        " scenario_id = ':scenarioId ",
                namedParameters, (ResultSetExtractor<LoadedContext>) new LoadedContextRowMapper()));
    }

    public boolean createContext(ActionContext actionContext, HistoryType historyType) {
        String sql = "INSERT INTO block_history VALUES (:blockId, :blockVersion, :scenarioId, :blockState, :historyType, " +
                " :producerId, :producerVersion, :values, :producerServiceName)";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate
                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));

        SqlParameterSource updateParams = new MapSqlParameterSource()
                .addValue("blockId", actionContext.getConsumerBlockId().getId())
                .addValue("blockVersion", actionContext.getConsumerBlockId().getVersion())
                .addValue("scenarioId", actionContext.getScenarioId())
                .addValue("blockState", BlockState.START.name())
                .addValue("historyType", historyType.name())
                .addValue("producerId", actionContext.getProducerBlockId().getId())
                .addValue("producerVersion", actionContext.getProducerBlockId().getVersion())
                .addValue("values", actionContext.getConsumerValuesJson())
                .addValue("producerServiceName", actionContext.getProducerServiceUrl());


        return namedParameterJdbcTemplate.update(sql, updateParams) == 1;
    }

    public Collection<LoadedContext> findNotCompletedContexts(Identifier blockId, HistoryType historyType) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate
                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("blockId", blockId.getId())
                .addValue("blockVersion", blockId.getVersion())
                .addValue("historyType", historyType.name());

        return namedParameterJdbcTemplate.query("SELECT * FROM block_history " +
                        " WHERE block_id = :actionId AND block_version = :blockVersion AND historyType = :historyType AND" +
                        " block_state != 'DONE' ",
                namedParameters, (RowMapper<LoadedContext>) new LoadedContextRowMapper());
    }

//    private boolean setActionState(UUID scenario, Long actionId, BlockState actionState, Transaction transaction) {
//        String updateSql = "UPDATE action_context SET action_state = :status " +
//                " where action_id = :actionId AND scenario_id = :scenarioId;";
//        NamedParameterJdbcTemplate namedParameterJdbcTemplate
//                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transaction.getDataSource()));
//        SqlParameterSource updateParams = new MapSqlParameterSource()
//                .addValue("actionId", actionId)
//                .addValue("scenarioId", scenario)
//                .addValue("status", actionState.name());
//        boolean result;
//        try {
//            result = namedParameterJdbcTemplate.update(updateSql, updateParams) == 1;
//        } catch (Exception e) {
//            if (e.getMessage().contains("not accepted state")) {
//                return false;
//            }
//            throw e;
//        }
//        return result;
//    }


//    @Override
//    public Optional<ActionContext> findActionContext(UUID scenarioId, BlockId blockId) {
//        ActionContext context = transactionManager.executeInTransaction(task -> {
//            NamedParameterJdbcTemplate namedParameterJdbcTemplate
//                    = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));
//
//            SqlParameterSource namedParameters = new MapSqlParameterSource()
//                    .addValue("scenarioId", scenarioId)
//                    .addValue("blockId", blockId.getBlockId());
//            ActionContext actionContext = namedParameterJdbcTemplate.queryForObject("SELECT * FROM action_history" +
//                            " WHERE scenario_id = :scenarioId AND current_block_id = :blockId",
//                    namedParameters, ActionContext.class);
//
//            return actionContext;
//        });
//
//        return Optional.ofNullable(context);
//    }
//
//    @Override
//    public boolean updateActionContextStatus(UUID scenarioId, BlockId blockId, BlockState newActionState) {
//        String blockingSQL = "SELECT * from action_contexts where action_id = :actionId" +
//                " AND scenario_id = :scenarioId FOR UPDATE";
//        NamedParameterJdbcTemplate namedParameterJdbcTemplate
//                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));
//        SqlParameterSource blockNamedParams = new MapSqlParameterSource()
//                .addValue("actionId", blockId.getBlockId())
//                .addValue("scenarioId", scenarioId);
//        namedParameterJdbcTemplate.queryForObject(blockingSQL, blockNamedParams, Void.class);
//
//
//        String selectCurrentActionStateSQL = "select action_state from action_contexts where action_id = :actionId " +
//                " AND scenario_id = :scenarioId";
//        BlockState currentActionState
//                = namedParameterJdbcTemplate.queryForObject(selectCurrentActionStateSQL, blockNamedParams, BlockState.class);
//
//        boolean isStateAcceptToUpdate = false;
//        if (newActionState == BlockState.INITIALIZING
//                && currentActionState != BlockState.INITIALIZING
//                && currentActionState != BlockState.EXECUTING
//                && currentActionState != BlockState.ERROR
//                && currentActionState != BlockState.COMPLETE) {
//            isStateAcceptToUpdate = true;
//        } else if (newActionState == BlockState.EXECUTING
//                && currentActionState != BlockState.EXECUTING
//                && currentActionState != BlockState.COMPLETE
//                && currentActionState != BlockState.ERROR) {
//            isStateAcceptToUpdate = true;
//        } else if (newActionState == BlockState.COMPLETE
//                && currentActionState != BlockState.COMPLETE
//                && currentActionState != BlockState.ERROR){
//            isStateAcceptToUpdate = true;
//        }else if (newActionState == BlockState.ERROR
//                && currentActionState != BlockState.COMPLETE
//                && currentActionState != BlockState.ERROR) {
//            isStateAcceptToUpdate = true;
//        }
//
//        if (!isStateAcceptToUpdate) return false;
//
//        String updateSQL = "UPDATE action_contexts SET action_state = :settingStatus " +
//                "where action_id = :actionId AND scenario_id = :scenarioId";
//        SqlParameterSource updateParams = new MapSqlParameterSource()
//                .addValue("actionId", blockId.getBlockId())
//                .addValue("scenarioId", scenarioId)
//                .addValue("settingStatus", newActionState);
//        int rowsUpdatedCount = namedParameterJdbcTemplate.update(updateSQL, updateParams);
//        return rowsUpdatedCount > 0;
//    }
//
//    @Override
//    public void updateActionContextVariables(UUID scenarioId, BlockId blockId, VariablesContainer variablesContainer) {
//        String sql = "UPDATE action_contexts SET executing_variables = :executingVariables " +
//                " where action_id = :actionId AND scenario_id = :scenarioId";
//        NamedParameterJdbcTemplate namedParameterJdbcTemplate
//                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));
//
//        SqlParameterSource updateParams = new MapSqlParameterSource()
//                .addValue("actionId", blockId.getBlockId())
//                .addValue("scenarioId", scenarioId)
//                .addValue("executingVariables", variablesContainer.toJson());
//        namedParameterJdbcTemplate.update(sql, updateParams);
//
//    }
//
//    @Override
//    public void updateActionContextOutputSignal(UUID scenarioId, BlockId blockId, SignalId outputSignalId) {
//        String sql = "UPDATE action_contexts SET output_signal_id  = :outputSignalId " +
//                " where action_id = :actionId AND scenario_id = :scenarioId";
//        NamedParameterJdbcTemplate namedParameterJdbcTemplate
//                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));
//        SqlParameterSource updateParams = new MapSqlParameterSource()
//                .addValue("actionId", blockId.getBlockId())
//                .addValue("scenarioId", scenarioId)
//                .addValue("outputSignalId", outputSignalId.getId());
//        namedParameterJdbcTemplate.update(sql, updateParams);
//    }
//
//    @Override
//    public boolean saveContext(ActionContext actionContext) {
//        String sql = "INSERT INTO action_contexts VALUES (:actionId, :scenarioId, :actionState, :executingVariables," +
//                " :requestBlockId, :requestServiceName, :outputSignalId)";
//        NamedParameterJdbcTemplate namedParameterJdbcTemplate
//                = new NamedParameterJdbcTemplate(Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));
//        SqlParameterSource updateParams = new MapSqlParameterSource()
//                .addValue("actionId", actionContext.getActionId().getBlockId())
//                .addValue("scenarioId", actionContext.getScenarioId())
//                .addValue("actionState", actionContext.getActionState())
//                .addValue("executingVariables", actionContext.getExecutingVariables().toJson())
//                .addValue("requestBlockId", actionContext.getRequestBlockId().getBlockId())
//                .addValue("requestServiceName", actionContext.getRequestServiceName())
//                .addValue("outputSignalId", actionContext.getOutputSignalId().getId());
//
//        return namedParameterJdbcTemplate.update(sql, updateParams) == 1;
//    }


//    /**
//     * создать новый конекст активности
//     *
//     * @param signal
//     * @return
//     */
//    public ActivityContext createNewActionContext(SignalDTO signal, Block actionBlock, String currentServiceName,
//                                                  Long currentServiceId ) {
//        throw new NullPointerException("NOT WORKING YET");

//        VariablesSynchRepo variablesSynchRepo = this;
//        return transactionManager.commitInTransaction(jpaTransactionManager -> {
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(
//                    Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));
//            KeyHolder keyHolder = new GeneratedKeyHolder();
//            String INSERT_MESSAGE_SQL = "INSERT INTO action_history VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//
//            jdbcTemplate.update(connection -> {
//                PreparedStatement ps = connection
//                        .prepareStatement(INSERT_MESSAGE_SQL, Statement.RETURN_GENERATED_KEYS);
//                ps.setObject(1, signal.getScenarioId()); //айди сценария
//                ps.setLong(2, actionBlock.getId().getBlockId()); //айди блока который сейчас исполняется
//                ps.setString(3, actionBlock.getId().getVersion().getVersionName()); // версия блока, которая сейчас исполняется
//                ps.setLong(4, 1);// транзакция
//                ps.setLong(5, currentServiceId);//айди текщуего сервиса
//                ps.setString(6, currentServiceName);//имя текущего сервиса
//                ps.setLong(7, signal.getProducerServiceId());//айди сервиса из которого была запущена активность
//                ps.setString(8, signal.getProducerServiceName());//имя сервиса из которого была запущена активность
//                ps.setLong(9, signal.getProducerBlockId().getBlockId());//айди блока из котрого была запущена активность
//                ps.setString(10, signal.getProducerBlockId().getVersion().getVersionName());//версия блока из котрого была запущена активность
//                ps.setLong(11, signal.getSignalId().getBlockId());//айди сигнала который запустил активность
//                ps.setString(12, signal.getSignalId().getVersion().getVersionName());//версия  сигнала который запустил активность
//                ps.setString(13, ActionState.INITIALIZING.toString());//текущий стейт
//                ps.setTimestamp(14, new Timestamp(System.currentTimeMillis()));//последнее время изменения
//                return ps;
//            }, keyHolder);
//
//            UUID actionUUID = (UUID) keyHolder.getKeyList().get(0).get("action_id");
//            return ActivityContext.builder()
//                    .actionId(actionUUID)
//                    .businessId(signal.getScenarioId())
//                    .currentBlockId(actionBlock.getId())
//                    .currentTransactionId(1l)
//                    .currentServiceId(currentServiceId)
//                    .currentServiceName(currentServiceName)
//                    .startSignal(signal)
//                    .currentState(ActionState.INITIALIZING)
//                    .pureVariableList(actionBlock.getPureVariableList())
//                    .variablesContainer(new VariablesContainerImpl(variablesSynchRepo, actionId))
//                    .build();
//        });

//    }
//    public boolean updateActionContext(UUID scenario, BlockId blockId, ActionState actionState) {
//        throw new NullPointerException("NOT WORKING YET");
//    }

//    @Override
//    public void commitValues(Map<Long, ProcessValue> processValueMap, UUID actionId) {
//        transactionManager.executeInTransaction((TransactionCallable<Void>) jpaTransactionManager -> {
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(
//                    Objects.requireNonNull(transactionManager.getJpaTransactionManager().getDataSource()));
//            String SQL = "INSERT INTO action_values VALUES(?,?,?)";
//            processValueMap.forEach((key,value) -> {
//                jdbcTemplate.update(conn -> {
//                    PreparedStatement pr = conn.prepareStatement(SQL);
//                    pr.setLong(1,key);
//                    pr.setObject(2, actionId);
//                    pr.setString(3,value.getJsonValue());
//                    return pr;
//                });
//            });
//
//            return null;
//        });
//    }
}
