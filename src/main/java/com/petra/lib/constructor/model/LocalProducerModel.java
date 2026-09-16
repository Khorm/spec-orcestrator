package com.petra.lib.constructor.model;

import com.petra.lib.utils.id.Identifier;
import lombok.Getter;

import java.util.*;
import java.util.function.Predicate;


public class LocalProducerModel {
    private Long id;
    private String version;

    @Getter
    private String name;

    @Getter
    private Collection<RemoteConsumerModel> consumers;

    @Getter
    private Collection<ValueModel> inputVariables;

    public Identifier geId(){
        return new Identifier(id, version);
    }

    public RemoteConsumerModel findFirst(){
        return  consumers.stream().filter(remoteConsumerModel
                -> remoteConsumerModel.getPreviousBlockId() == null).findFirst().orElseThrow();
    }

//    public Collection<RemoteConsumerModel> getConsumers(){
//        Long searchedId = id;
//        List<RemoteConsumerModel> sortedConsumers = new ArrayList<>();
//
//        ArrayList<RemoteConsumerModel> iterList = new ArrayList<>(consumers);
//        ListIterator<RemoteConsumerModel> iterator = iterList.listIterator();
//        while (iterator.hasNext()){
//            RemoteConsumerModel consumer = iterator.next();
//            if (consumer.getPreviousBlockId() != null && consumer.getPreviousBlockId().equals(searchedId)){
//                searchedId = consumer.getId();
//                sortedConsumers.add(consumer);
//                iterator = iterList.listIterator();
//            }
//        }
//        return sortedConsumers;
//    }
}
