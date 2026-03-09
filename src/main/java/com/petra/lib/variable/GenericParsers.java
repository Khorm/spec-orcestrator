package com.petra.lib.variable;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class GenericParsers {
    private static final Map<Class<?>, Function<String, ?>> PARSERS = new HashMap<>();

    static {
        PARSERS.put(String.class, s -> s);
        PARSERS.put(Integer.class, Integer::parseInt);
        PARSERS.put(Long.class, Long::parseLong);
        PARSERS.put(Double.class, Double::parseDouble);
        PARSERS.put(BigDecimal.class, BigDecimal::new);
        PARSERS.put(Boolean.class, Boolean::parseBoolean);
        PARSERS.put(LocalDate.class, LocalDate::parse);
        PARSERS.put(LocalDateTime.class, LocalDateTime::parse);
    }

    public static Function<String, ?> getParser(Class<?> clazz){
        Function<String, ?> parser = PARSERS.get(clazz);
        if (parser == null) {
            return (Function<String, Object>) s -> new ObjectMapper().convertValue(s, clazz);
        }
        return parser;
    }
}
