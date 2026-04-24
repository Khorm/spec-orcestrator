package com.petra.lib.constructor.model;

import lombok.Getter;

import java.util.*;

@Getter
public class LocalProducerModel {
    private Long id;
    private String version;

    private String name;
    private Collection<RemoteConsumerModel> consumers;

    private Collection<ValueLoaderDto> exitValues;
    private Collection<ValueModel> contextValues;

    public Collection<RemoteConsumerModel> getConsumers(){
        Long searchedId = id;
        List<RemoteConsumerModel> sortedConsumers = new ArrayList<>();

        ArrayList<RemoteConsumerModel> iterList = new ArrayList<>(consumers);
        ListIterator<RemoteConsumerModel> iterator = iterList.listIterator();
        while (iterator.hasNext()){
            RemoteConsumerModel consumer = iterator.next();
            if (consumer.getPreviousBlockId() != null && consumer.getPreviousBlockId().equals(searchedId)){
                searchedId = consumer.getId();
                sortedConsumers.add(consumer);
                iterator = iterList.listIterator();
            }
        }
        return sortedConsumers;
    }
}
