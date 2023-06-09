package com.petra.lib.worker.variable.group;

import com.petra.lib.annotation.MappingHandler;
import com.petra.lib.manager.block.BlockId;
import com.petra.lib.signal.request.controller.SignalRequestManager;
import com.petra.lib.variable.mapper.VariableMapper;
import com.petra.lib.variable.mapper.VariableMapperFactory;
import com.petra.lib.worker.variable.group.handler.VariableHandler;
import com.petra.lib.worker.variable.group.loaders.VariableLoader;
import com.petra.lib.worker.variable.group.repo.ContextRepo;
import com.petra.lib.worker.variable.model.VariableGroupModel;

import java.util.Collection;
import java.util.stream.Collectors;

class GroupFabric {

    static VariableGroup createGroup(SignalRequestManager signalRequestManager, VariableGroupModel variableGroupModel, Collection<VariableHandler> handlers,
                                     ContextRepo contextRepo, BlockId blockId, String blockName) {

        SourceRequestManager sourceRequestManager = createSourceLoader(signalRequestManager, variableGroupModel, contextRepo, blockId);
        Collection<VariableLoader> variableLoaders = createVariableLoader(variableGroupModel, handlers, blockName);

        return new VariableGroup(
                variableGroupModel.getParentGroups(),
                variableGroupModel.getGroupId(),
                variableGroupModel.getChildGroups(),
                sourceRequestManager,
                variableLoaders
        );
    }

    private static SourceRequestManager createSourceLoader(SignalRequestManager signalRequestManager, VariableGroupModel variableGroupModel,
                                                           ContextRepo contextRepo, BlockId blockId) {
        VariableMapper variableMapper = VariableMapperFactory.createVariableMapper(variableGroupModel.getSourceSignal().getToSourceSignalVariablesMap());
        return new SourceRequestManager(signalRequestManager, variableMapper, contextRepo, blockId, variableGroupModel.getSourceSignal().getSourceSignalId());
    }

    private static Collection<VariableLoader> createVariableLoader(VariableGroupModel variableGroupModel, Collection<VariableHandler> handlers,
                                                                   String blockName) {
        return variableGroupModel.getGroupVariables().stream()
                .map(groupVariable -> {
                    switch (groupVariable.getVariableType()) {
                        case SOURCE:
                            return VariableLoader.getSourceVariableLoader(groupVariable.getVariableId(), groupVariable.getProducerVariableId());
                        case INIT:
                            return VariableLoader.getInitVariableLoader(groupVariable.getVariableId(), groupVariable.getProducerVariableId());
                        case HANDLER:
                            for (VariableHandler variableHandler : handlers) {
                                String blockNameHandler = variableHandler.getClass().getAnnotation(MappingHandler.class).blockName();
                                String varNameHandler = variableHandler.getClass().getAnnotation(MappingHandler.class).variableName();
                                if (groupVariable.getVariableName().equals(varNameHandler) &&
                                        blockNameHandler.equals(blockName)) {
                                    return VariableLoader.getHandlerVariable(groupVariable.getVariableId(), variableHandler);
                                }
                            }
                            throw new NullPointerException("handler for variable " + groupVariable.getVariableName() + " " + blockName + " not found");
                    }
                    throw new NullPointerException("Variable type not found");
                }).collect(Collectors.toList());
    }

//    private static GroupHandler createGroupHandler(VariableGroupModel variableGroupModel, Collection<VariableHandler> handlers, String blockName){
//        List<GroupHandler.HandlerKeeper> groupHandlers = handlers.stream()
//                .flatMap(handler -> {
//                    String blockNameHandler = handler.getClass().getAnnotation(MappingHandler.class).blockName();
//                    String varNameHandler = handler.getClass().getAnnotation(MappingHandler.class).variableName();
//                    if (!blockNameHandler.equals(blockName)){
//                        return null;
//                    }
//                    for (VariableModel variableModel : variableGroupModel.getGroupVariables()){
//                        if (variableModel.getVariableName().equals(varNameHandler)){
//                            return Stream.of(new GroupHandler.HandlerKeeper(variableModel.getVariableId(), handler));
//                        }
//                    }
//                    throw new Error("Variable handler not found");
//                }).collect(Collectors.toList());
//        return new GroupHandler(groupHandlers);
//    }
}
