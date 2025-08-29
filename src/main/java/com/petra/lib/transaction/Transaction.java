package com.petra.lib.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


public class Transaction {
    private final TransactionStatus transactionStatus;
    private final JpaTransactionManager jpaTransactionManager;
    private boolean commitTransaction = true;

    public Transaction(TransactionStatus transactionStatus, JpaTransactionManager jpaTransactionManager) {
        this.transactionStatus = transactionStatus;
        this.jpaTransactionManager = jpaTransactionManager;
    }

    public void rollback(){
        commitTransaction = false;
    }

    public DataSource getDataSource(){
        return jpaTransactionManager.getDataSource();
    }

    public EntityManagerFactory getEntityManagerFactory(){
        return jpaTransactionManager.getEntityManagerFactory();
    }

    boolean isTransactionSuccess(){
        return commitTransaction;
    }


}
