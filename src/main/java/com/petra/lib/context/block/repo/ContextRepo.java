package com.petra.lib.context.block.repo;

import com.petra.lib.context.block.ContextEntity;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.transaction.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContextRepo {

    boolean insertContext(ContextEntity context, Transaction transaction);

    Optional<ContextEntity> findContext(UUID scenarioId, Identifier consumerId, Transaction transaction, boolean isBlocking);
    void save(ContextEntity entity, Transaction transaction);

    List<ContextEntity> getNotFinishedContexts(String serviceName, Integer maxTimeSeconds, Transaction tr);



}
