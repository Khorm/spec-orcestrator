package com.petra.lib.variable.loader.impl;

import com.petra.lib.constructor.model.ValueLoaderDto;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.variable.container.ValueDto;
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
    ValueLoaderDto valueModel;

    //паренты текущей переменной
    List<Long> parents;

    //чилды текущей переменной
    List<ValueLoader> children;

    protected LoaderAbs(ThreadController threadController, ValueLoaderDto valueModel,
                        List<Long> parents, List<ValueLoader> children) {

        this.threadController = threadController;
        this.valueModel = valueModel;
        this.parents = parents;
        this.children = children;
    }

    public void load(ValueContext context) {
        if (!context.isValueAcceptToExecute(parents, valueModel.getId())) {
            log.warn("{} Skip loading of {}, because it's not accept to run",
                    context.getScenarioId(), valueModel.getName());
            return;
        }
        log.info("{} Loading variable {}",context.getScenarioId(),valueModel.getName());
        threadController.executeLimitedPoolTask(() -> {
            try {
                ValueDto result = executeLoad(context);
                boolean isExit = context.registerLoadedValue(result);
                log.info("{} Variable {} loaded",context.getScenarioId(), valueModel.getName());
                if (isExit) return;
                children.forEach(child -> child.load(context));
            }catch (Exception e){
                log.error("{} variable load error: {} {}", context.getScenarioId(), valueModel.getName(), e);
                e.printStackTrace();
                context.error(e);
            }
        });
    }

    protected abstract ValueDto executeLoad(ValueContext context);

    public ValueLoaderDto getValueModel() {
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
