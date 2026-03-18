package com.petra.lib.transaction;


import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.Isolation;

public interface TransactionManager {
    <T> T executeInTransaction(TransactionCallable<T> task, Isolation transactionDefinition);
    <T> T executeInTransaction(TransactionCallable<T> task);
    <T> T executeInTransaction(TransactionCallable<T> task, Transaction transaction);

    void executeInTransaction(TransactionRunnable task, Isolation transactionDefinition);
    void executeInTransaction(TransactionRunnable task, Transaction transaction);

    Transaction openNewTransaction();
//    void commitTransaction();
//    void rollback();

    JpaTransactionManager getJpaTransactionManager();


}
