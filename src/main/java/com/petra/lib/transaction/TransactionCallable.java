package com.petra.lib.transaction;

public interface TransactionCallable<T> {
    T run(Transaction transaction) throws Exception;
}
