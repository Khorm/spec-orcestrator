package com.petra.lib.actor;

import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.operation.OperationService;
import com.petra.lib.operation.operations.executor.UserActionContextImpl;
import com.petra.lib.operation.operations.executor.handler.UserActionHandler;
import com.petra.lib.transaction.Transaction;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.value.Value;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalConsumer {

    @Getter
    Identifier identifier;
    @Getter
    BlockType blockType;

    @Getter
    String name;

    @Getter
    List<ValueModel> inputVariables;
    @Getter
    List<ValueModel> outputVariables;
    UserActionHandler userActionHandler;
    TransactionManager transactionManager;



    LocalConsumer(Identifier identifier, BlockType blockType, String name,
                  List<ValueModel> inputVariables, List<ValueModel> outputVariables, UserActionHandler userActionHandler,
                  TransactionManager transactionManager) {
        this.identifier = identifier;
        this.blockType = blockType;
        this.name = name;

        this.inputVariables = Collections.unmodifiableList(inputVariables);
        this.outputVariables = Collections.unmodifiableList(outputVariables);
        this.userActionHandler = userActionHandler;
        this.transactionManager = transactionManager;

    }

    public boolean start(Context context, RemoteProducer remoteProducer, OperationService operationService){
        return transactionManager.executeInTransaction(tr -> {
            ValueContainer outContainer = ValueContainerFactory.getSimpleContainer(outputVariables);
            boolean isNewCreated = context.insert(remoteProducer, blockType,
                    ContextState.STARTED, outContainer, tr);


            context.lockAndLoad(tr);
            if (context.getCurrentState() != ContextState.STARTED) {
                tr.rollback();
                log.info("[{}] Repeating {} - {}", context.getScenarioId(), name, blockType);
                return false;
            }

            log.info("[{}] Starting {} - {}", context.getScenarioId(), name, blockType);
            operationService.executeState(context);
            context.save(tr);
            return true;
        });
    }

    public void execute(Context blockContext, OperationService operationService, TransactionManager transactionManager) {
//        transactionManager.executeInTransaction(transaction -> {
            try(Transaction transaction = transactionManager.createNewTransaction(false, userActionHandler.getTransactionIsolationLevel())) {
                log.debug("{} starting transaction for action {}", blockContext.getScenarioId(), name);

                EntityManager entityManager = transactionManager.getEntityManager();
                List<Value> contextValues =  blockContext.getContextInputValues().getValues();
                UserActionContextImpl userContext = new UserActionContextImpl(entityManager,
                        contextValues, outputVariables);
                userActionHandler.execute(userContext);
                blockContext.lockAndLoad(transaction);
                boolean isResultSet = blockContext.setState(ContextState.EXECUTED);
                if (!isResultSet){
                    transaction.rollback();
                    return;
                }
                blockContext.setOutValues(userContext.getOutputValues());
                blockContext.setExecutionStatus(ExecutionStatus.OK);
                blockContext.save(transaction);
                transaction.commit();
                log.debug("{} action {} context saved successfully",blockContext.getScenarioId(), name);
                operationService.executeState(blockContext);

            } catch (Exception e) {
                try(Transaction transaction = transactionManager.createNewTransaction(false, null)) {
                    log.error("{} Exception occurred during action {} execution {}",
                            blockContext.getScenarioId(),name, e);
                    boolean isResultSet = blockContext.setState(ContextState.EXECUTED);
                    if (!isResultSet){
                        transaction.rollback();
                        return;
                    }
                    blockContext.setExecutionStatus(ExecutionStatus.ERROR);
                    blockContext.save(transaction);
                    transaction.commit();
                    operationService.executeState(blockContext);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
//        }, userActionHandler.getTransactionIsolationLevel());

    }
}
