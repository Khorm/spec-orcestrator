package com.petra.lib.variable.loader.impl;

import com.petra.lib.constructor.model.ValueModelDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.context.ValueContext;
import com.petra.lib.variable.loader.ValueLoader;
import com.petra.lib.variable.value.Value;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public abstract class LoaderAbs implements ValueLoader {

    ThreadController threadController;
    ValueModelDto valueModel;
    List<Long> parents;
    List<ValueLoader> children;

    protected LoaderAbs(ThreadController threadController, ValueModelDto valueModel,
                        List<Long> parents, List<ValueLoader> children) {

        this.threadController = threadController;
        this.valueModel = valueModel;
        this.parents = parents;
        this.children = children;
    }

    public void load(ValueContext context) {
        if (!context.areValuesLoaded(parents)) {
            return;
        }
        log.info("Loading variable {}",valueModel.getName());
        threadController.executeUnlimitedPoolTask(() -> {
            Value result = executeLoad(context);
            boolean isExit = context.registerLoadedValue(result);
            log.info("Variable {} loaded", valueModel.getName());
            if (isExit) return;
            children.forEach(child -> child.load(context));
        });
    }

    protected abstract Value executeLoad(ValueContext context);

    public ValueModelDto getValueModel() {
        return valueModel;
    }

    public List<Long> getParents() {
        return parents;
    }

    public Long getVariableId() {
        return valueModel.getId();
    }

    public String getVariableName(){
        return valueModel.getName();
    }


}
