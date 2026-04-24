package com.petra.lib.transaction;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


@Log4j2
public class Transaction  implements AutoCloseable{
    private TransactionStatus transactionStatus;
    private final JpaTransactionManager jpaTransactionManager;
    private final DefaultTransactionDefinition definition;
    private boolean isCommit = true;

    public Transaction(JpaTransactionManager jpaTransactionManager, DefaultTransactionDefinition definition,
                       TransactionStatus transactionStatus) {
        this.jpaTransactionManager = jpaTransactionManager;
        this.definition = definition;
        this.transactionStatus = transactionStatus;
    }


    public void rollback(){
        isCommit = false;
    }

    public void commit(){
        isCommit = true;
    }

    public void reOpenTransaction(){
        jpaTransactionManager.rollback(transactionStatus);
        isCommit = false;
        transactionStatus = jpaTransactionManager.getTransaction(definition);
    }

    public void openNewTransaction() {
        if (transactionStatus != null){
            throw new IllegalStateException("You must call the method 'reOpenTransaction' if your transaction already opened");
        }
        transactionStatus = jpaTransactionManager.getTransaction(definition);
    }

    public DataSource getDataSource(){
        return jpaTransactionManager.getDataSource();
    }

    public EntityManagerFactory getEntityManagerFactory(){
        return jpaTransactionManager.getEntityManagerFactory();
    }

    @Override
    public void close() throws Exception {
        if (isCommit){
            jpaTransactionManager.commit(transactionStatus);
        }else {
            jpaTransactionManager.rollback(transactionStatus);
        }


    }


}
