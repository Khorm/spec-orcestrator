package com.petra.lib.constructor.model;

import java.util.Collection;

public class ConstructorModel {
    private Collection<LocalActivityModel> activities;
    private Collection<LocalConditionModel> conditions;
    private Collection<LocalSourceModel> sources;
    private Collection<LocalProducerModel> workflows;

    public Collection<LocalActivityModel> getActivities() {
        return activities;
    }

    public Collection<LocalConditionModel> getConditions(){
        return conditions;
    }

    public Collection<LocalSourceModel> getSources() {
        return sources;
    }

    public Collection<LocalProducerModel> getWorkflows() {
        return workflows;
    }
}
