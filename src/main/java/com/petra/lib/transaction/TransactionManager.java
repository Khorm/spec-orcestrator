package com.petra.lib.transaction;


import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.Isolation;

import javax.persistence.EntityManager;

public interface TransactionManager {
//    <T> T executeInTransaction(TransactionCallable<T> task, Isolation transactionDefinition);
//    <T> T executeInTransaction(TransactionCallable<T> task);
    <T> T executeInTransaction(TransactionCallable<T> task, Transaction transaction);

//    void executeInTransaction(TransactionRunnable task, Isolation transactionDefinition);
    void executeInTransaction(TransactionRunnable task, Transaction transaction);

    void executeInTransaction(Boolean readOnly, Isolation transactionDefinition, TransactionRunnable runnable);
//    <T> T executeInTransaction(Boolean readOnly, Isolation transactionDefinition, TransactionCallable<T> runnable);

    Transaction createNewTransaction(Boolean readOnly, Isolation transactionDefinition);
//    void commitTransaction();
//    void rollback();

    JpaTransactionManager getJpaTransactionManager();

    EntityManager getEntityManager();


}
