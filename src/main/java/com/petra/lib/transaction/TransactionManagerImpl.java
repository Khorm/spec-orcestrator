package com.petra.lib.transaction;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

class TransactionManagerImpl implements TransactionManager {
    private final JpaTransactionManager jpaTransactionManager;

    public TransactionManagerImpl(JpaTransactionManager jpaTransactionManager) {
        this.jpaTransactionManager = jpaTransactionManager;
    }


//    @Override
//    public <T> T executeInTransaction(TransactionCallable<T> task, Transaction transaction) {
//        return task.run(transaction);
//    }
//
//
//    @Override
//    public void executeInTransaction(TransactionRunnable task, Transaction transaction) {
//        task.run(transaction);
//    }
//
//    @Override
//    public void executeInTransaction(Boolean readOnly, Isolation transactionDefinition, TransactionRunnable runnable){
//        try(Transaction transaction = createNewTransaction(readOnly, transactionDefinition)){
//            runnable.run(transaction);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public Transaction createNewTransaction(Boolean readOnly, Isolation transactionDefinition) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setIsolationLevel(Objects.requireNonNullElse(transactionDefinition, Isolation.DEFAULT).value());
        definition.setReadOnly(Objects.requireNonNullElse(readOnly, false));
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus transactionStatus = jpaTransactionManager.getTransaction(definition);
        return new Transaction(jpaTransactionManager, definition, transactionStatus);
    }

    @Override
    public <T> T executeInTransaction(Function<Transaction, T> tr) {
        try (Transaction transaction = createNewTransaction(false, null)) {
            return tr.apply(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T executeInTransaction(Function<Transaction, T> tr, Isolation transactionDefinition) {
        try (Transaction transaction = createNewTransaction(false, transactionDefinition)) {
            return tr.apply(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T executeInTransactionReadOnly(Function<Transaction, T> tr) {
        try (Transaction transaction = createNewTransaction(true, null)) {
            return tr.apply(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeInTransaction(Consumer<Transaction> tr) {
        try (Transaction transaction = createNewTransaction(false, null)) {
            tr.accept(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeInTransaction(Consumer<Transaction> tr, Isolation transactionDefinition) {
        try (Transaction transaction = createNewTransaction(false, transactionDefinition)) {
            tr.accept(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeInTransactionReadOnly(Consumer<Transaction> tr) {
        try (Transaction transaction = createNewTransaction(true, null)) {
            tr.accept(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


//    public JpaTransactionManager getJpaTransactionManager() {
//        return jpaTransactionManager;
//    }

    @Override
    public EntityManager getEntityManager() {
        return jpaTransactionManager.getEntityManagerFactory().createEntityManager();
    }

}
