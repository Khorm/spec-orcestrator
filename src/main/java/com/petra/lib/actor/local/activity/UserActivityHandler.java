package com.petra.lib.actor.local.activity;

import org.springframework.transaction.annotation.Isolation;

public interface UserActivityHandler {

    void execute(UserActivityContext variableUserActivityContext);

    Isolation getTransactionIsolationLevel();
}
