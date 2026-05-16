package com.petra.lib.variable.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.PetraException;
import com.petra.lib.variable.GenericParsers;
import com.petra.lib.variable.container.ValueDto;

import java.util.List;

@Deprecated
class SimpleValue /*extends ValueAbs*/{


//    SimpleValue(ValueDto model) {
//        super(model);
//    }
//
//    @Override
//    public <T> List<T> getParsedList(Class<T> clazz) {
//        throw new PetraException("Trying to get list from Object value " + model.getName());
//    }
//
//    @Override
//    public <T> T getParsedValue(Class<T> clazz) {
//        ObjectMapper oj = new ObjectMapper();
//        try {
//            return oj.readValue(model.getJsonValue(), clazz);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Value cloneValue() {
//        return new SimpleValue(getModel());
//    }

}
