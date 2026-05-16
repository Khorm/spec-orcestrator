package com.petra.lib.constructor.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class LocalConditionModel {
    private Long id;
    private String version;
    private String blockType;
    private String name;
    private String script;

    private List<ValueModel> inputModels;
}
