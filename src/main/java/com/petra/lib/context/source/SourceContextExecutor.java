package com.petra.lib.context.source;

import com.petra.lib.context.model.Identifier;
import com.petra.lib.context.model.LocalSource;
import com.petra.lib.context.model.RemoteProducer;
import com.petra.lib.variable.container.ValueContainer;

import javax.persistence.EntityManagerFactory;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SourceContextExecutor {

    private final Map<Identifier, LocalSource> localSourceMap;
    private final EntityManagerFactory entityManagerFactory;

    public SourceContextExecutor(Collection<LocalSource> localSources, EntityManagerFactory entityManagerFactory) {

        this.localSourceMap = localSources.stream().collect(Collectors.toMap(LocalSource::getIdentifier, Function.identity()));
        this.entityManagerFactory = entityManagerFactory;
    }

    public ValueContainer startContext(RemoteProducer remoteProducer) {
        LocalSource execSource = localSourceMap.get(remoteProducer.getConsumerId());
        SourceContext sourceContext = new SourceContext(remoteProducer.getValuesContainer(), execSource, entityManagerFactory);
        return sourceContext.execute();
    }
}
