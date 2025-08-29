package com.petra.lib.variable;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    }

    public static Function<String, ?> getParser(Class<?> clazz){
        return PARSERS.get(clazz);
    }
}
