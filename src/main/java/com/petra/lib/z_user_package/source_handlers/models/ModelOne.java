package com.petra.lib.z_user_package.source_handlers.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

@Getter
@Setter
@ToString
public class ModelOne {
    private Long InnerOne;
    private Collection<String> InnerTwo;
}
