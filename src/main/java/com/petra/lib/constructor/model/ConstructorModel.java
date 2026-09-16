package com.petra.lib.constructor.model;

import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstructorModel {
    private Collection<LocalConsumerModel> consumers;
    private Collection<LocalSourceModel> sources;
    private Collection<LocalProducerModel> workflows;

}
