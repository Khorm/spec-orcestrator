package com.petra.lib.transaction;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

class TransactionManagerImpl implements TransactionManager {
    private final JpaTransactionManager jpaTransactionManager;

    public TransactionManagerImpl(JpaTransactionManager jpaTransactionManager) {
        this.jpaTransactionManager = jpaTransactionManager;
    }


    public <T> T executeInTransaction(TransactionCallable<T> task, Isolation transactionDefinition) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setIsolationLevel(transactionDefinition.value());
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transactionStatus = jpaTransactionManager.getTransaction(definition);
        try {
            Transaction transaction = new Transaction(transactionStatus, jpaTransactionManager);
            T result = task.run(transaction);
            transactionAccept(transactionStatus, transaction);
            return result;
        } catch (Exception e) {
            jpaTransactionManager.rollback(transactionStatus);
            throw e;
        }
    }


    @Override
    public <T> T executeInTransaction(TransactionCallable<T> task) {
        return executeInTransaction(task, Isolation.READ_COMMITTED);
    }

    @Override
    public <T> T executeInTransaction(TransactionCallable<T> task, Transaction transaction) {
        return task.run(transaction);
    }

    @Override
    public void executeInTransaction(TransactionRunnable task, Isolation transactionDefinition) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transactionStatus = jpaTransactionManager.getTransaction(definition);

        Transaction transaction = new Transaction(transactionStatus, jpaTransactionManager);
        try {
            task.run(transaction);
            transactionAccept(transactionStatus, transaction);
        } catch (Exception e) {
            jpaTransactionManager.rollback(transactionStatus);
            throw e;
        }
    }

    @Override
    public void executeInTransaction(TransactionRunnable task, Transaction transaction) {
        task.run(transaction);
    }

    @Override
    public Transaction openNewTransaction() {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transactionStatus = jpaTransactionManager.getTransaction(definition);

        return new Transaction(transactionStatus, jpaTransactionManager);
    }


    public JpaTransactionManager getJpaTransactionManager() {
        return jpaTransactionManager;
    }

    private void transactionAccept(TransactionStatus transactionStatus, Transaction transaction ){
        jpaTransactionManager.commit(transactionStatus);
    }
}
