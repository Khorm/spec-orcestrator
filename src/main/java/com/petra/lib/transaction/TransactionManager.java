package com.petra.lib.transaction;


import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.Isolation;

import javax.persistence.EntityManager;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TransactionManager {
//    <T> T executeInTransaction(TransactionCallable<T> task, Isolation transactionDefinition);
//    <T> T executeInTransaction(TransactionCallable<T> task);
//    <T> T executeInTransaction(TransactionCallable<T> task, Transaction transaction);

//    void executeInTransaction(TransactionRunnable task, Isolation transactionDefinition);
//    void executeInTransaction(TransactionRunnable task, Transaction transaction);

//    void executeInTransaction(Boolean readOnly, Isolation transactionDefinition, TransactionRunnable runnable);
//    <T> T executeInTransaction(Boolean readOnly, Isolation transactionDefinition, TransactionCallable<T> runnable);

    Transaction createNewTransaction(Boolean readOnly, Isolation transactionDefinition);

    <T> T executeInTransaction(Function<Transaction, T> tr);
    <T> T executeInTransaction(Function<Transaction, T> tr, Isolation transactionDefinition);
    <T> T executeInTransactionReadOnly(Function<Transaction, T> tr);

    void executeInTransaction(Consumer<Transaction> tr);
    void executeInTransaction(Consumer<Transaction> tr, Isolation transactionDefinition);
    void executeInTransactionReadOnly(Consumer<Transaction> tr);

//    JpaTransactionManager getJpaTransactionManager();

    EntityManager getEntityManager();


}
