package com.petra.lib.actor.local.condition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.actor.local.LocalConsumer;
import com.petra.lib.constructor.model.ValueModel;
import com.petra.lib.context.block.Context;
import com.petra.lib.context.enums.BlockType;
import com.petra.lib.context.enums.ContextState;
import com.petra.lib.context.enums.ExecutionStatus;
import com.petra.lib.operation.OperationService;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.utils.id.Identifier;
import com.petra.lib.variable.container.ValueContainer;
import com.petra.lib.variable.container.ValueContainerFactory;
import com.petra.lib.variable.container.ValueDto;
import com.petra.lib.variable.enums.Multiplicity;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalCondition implements LocalConsumer {

    Identifier identifier;
    String VALUE_NAME = "result";

    @Getter
    List<ValueModel> inputVariables;
    String name;
    ConditionUserHandler conditionUserHandler;
    TransactionManager transactionManager;

    String script;


    public LocalCondition(Identifier identifier,
                          List<ValueModel> inputVariables, String name, ConditionUserHandler userActionHandler,
                          TransactionManager transactionManager, String script) {
        this.identifier = identifier;
        this.inputVariables = Collections.unmodifiableList(inputVariables);
        this.name = name;
        this.conditionUserHandler = userActionHandler;
        this.transactionManager = transactionManager;

        this.script = script;
    }


    @Override
    public Identifier getId() {
        return identifier;
    }

    @Override
    public String getName() {
        return "Condition " + identifier.toString();
    }

    public void execute(Context blockContext, OperationService operationService) {
        log.debug("{} starting  condition", blockContext.getScenarioId());

        ValueContainer outContainer = ValueContainerFactory.getSimpleContainerByModels(inputVariables);
        if (script == null) {
            EntityManager entityManager = transactionManager.getEntityManager();
            ConditionUserContextImpl userContext = new ConditionUserContextImpl(entityManager, blockContext.getContextInputValues());
            conditionUserHandler.execute(userContext);
            ObjectMapper om = new ObjectMapper();
            ValueDto valueDto;
            try {
                valueDto = new ValueDto(-1L, VALUE_NAME, Multiplicity.SINGLE,om.writeValueAsString(userContext.getAcceptNumber()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            outContainer.addValue(valueDto);
        } else {
            String valuesScript = blockContext.getContextInputValues().getValues().stream().map(value -> "def " +
                    value.getName() +
                    " = slurper.parseText('" +
                    value.getJsonValue() +
                    "') ; ").collect(Collectors.joining());

            ValueDto valueDto = getValueDto(valuesScript);
            outContainer.setValueJson(valueDto.getId(), valueDto.getJsonValue());
        }

        boolean isResultSet = blockContext.setState(ContextState.EXECUTED);

        blockContext.setOutValues(outContainer);
        blockContext.setExecutionStatus(ExecutionStatus.OK);

        log.debug("{} condition context saved successfully", blockContext.getScenarioId());
        operationService.executeState(blockContext);

    }

    @Override
    public BlockType getBlockType() {
        return BlockType.CONDITION;
    }

    private ValueDto getValueDto(String valuesScript) {
        String resultScript = "import com.fasterxml.jackson.databind.ObjectMapper; " +
                " def slurper = new groovy.json.JsonSlurper(); " +
                " def result; "
                + valuesScript
                + script
                + "; def oj = new ObjectMapper(); "
                + " return oj.writeValueAsString(result); ";

        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        String result = (String) shell.evaluate(resultScript);
        return new ValueDto(0L, "result", Multiplicity.SINGLE, result);
    }
}
