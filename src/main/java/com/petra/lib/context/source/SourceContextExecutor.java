package com.petra.lib.context.source;

import com.petra.lib.utils.id.Identifier;
import com.petra.lib.operation.actor.LocalSource;
import com.petra.lib.remote.dto.SourceRequestDto;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;

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

    public ValueContainer startContext(SourceRequestDto sourceRequestDto) {
        Identifier sourceId = new Identifier(sourceRequestDto.getConsumerSourceId(),
                sourceRequestDto.getConsumerSourceVersion());
        LocalSource execSource = localSourceMap.get(sourceId);

        SourceContext sourceContext = new SourceContext(ValueContainerFactory
                .getImmutableContainer(sourceRequestDto.getInputValues()),
                execSource, entityManagerFactory);
        return sourceContext.execute();
    }
}
