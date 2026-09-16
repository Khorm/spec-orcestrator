package com.petra.lib.constructor.model;

import com.petra.lib.context.enums.BlockType;
import com.petra.lib.variable.container.ValueDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

/**
 * Принимающие сообщения блоки. Активности либо воркфлоу
 */
@AllArgsConstructor
@Getter
public class LocalConsumerModel {
    private Long id;
    private String version;
    private BlockType consumerType;
    private String name;

    private Collection<ValueModel> inputModels;
    private Collection<ValueModel> outputModels;


}
