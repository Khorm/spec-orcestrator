package com.petra.lib.constructor.model;

import lombok.Getter;

import java.util.Collection;

@Getter
public class RemoteConsumerModel {
    private Long id;
    private String version;
    private String serviceName;

    private Long workflowId;
    private String workflowVersion;
    private Long nextBlockId;
    private Long previousBlockId;
    private String consumerName;

    /**
     * Коллекция загружаемых переменных
     */
    private Collection<ValueLoaderDto> loadedValues;

    /**
     * Коллекция всех переменных включая загружаемые
     */
    private Collection<ValueModel> contextValues;


}
