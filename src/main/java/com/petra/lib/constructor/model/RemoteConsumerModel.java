package com.petra.lib.constructor.model;

import com.petra.lib.context.enums.BlockType;
import com.petra.lib.utils.id.Identifier;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;

@Getter
public class RemoteConsumerModel {

    //id consumer
    private Long id;

    //version consumer
    private String version;
    //имя консумера
    private String consumerName;
//    private Long scenarioBlockId;

    // имя сервиса к которому относится консумер
    private String serviceName;
    private BlockType scenarioBlockType;

    //айди воркфлоу к которой относится консумер
    private Long workflowId;

    //версия воркфлоу к которой относится консумер
    private String workflowVersion;

    //айди следующего блока
    private Long nextBlockId;

    //версия следующего блока
    private String nextBlockVersion;
    private Long previousBlockId;
    private String previousBlockVersion;


    private Map<Integer, Identifier> nextBlocksByIdCondition;

    //переменные доступные в текущем контексте
    private Collection<ValueModel> contextValues;

    //загружаемые в текущем блоке переменные
    private Collection<ValueLoaderDto> loadValues;

    public Identifier getPrevBlockId(){
        return new Identifier(previousBlockId, previousBlockVersion);
    }

    public Identifier getId(){
        return new Identifier(id, version);
    }


}
