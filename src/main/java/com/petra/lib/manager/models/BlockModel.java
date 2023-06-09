package com.petra.lib.manager.models;

import com.petra.lib.manager.block.BlockId;
import com.petra.lib.signal.SignalId;
import com.petra.lib.signal.model.Version;
import com.petra.lib.variable.base.Variable;
import com.petra.lib.variable.mapper.MapperVariableModel;
import com.petra.lib.worker.variable.model.VariableGroupModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
public class BlockModel {
    /**
     * Block id
     */
    Long id;

    Version version;

    BlockType blockType;

    /**
     * Block name
     */
    String name;

    Collection<SignalId> initSignals;

    /**
     * Block execution variables
     */
    Collection<Variable> variables;

    /**
     * Groups for load variables variables
     */
    Collection<VariableGroupModel> variableGroups;

    /**
     * Mappers for response signals
     */
    Collection<MapperVariableModel> mappedVariablesToResponseSignals;

    public BlockId getBlockId() {
        return new BlockId(id, version);
    }

//    /**
//     * block requires sources
//     */
//    Collection<SourceSignalModel> sources;
//
//    /**
//     * Starter signals
//     */
//    Collection<SignalModel> requestSignals;
//
//    /**
//     * AnswerSignal
//     */
//    SignalModel responseSignal;
}
