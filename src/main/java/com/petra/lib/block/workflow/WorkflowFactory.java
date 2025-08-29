package com.petra.lib.block.workflow;

import com.petra.lib.block.Block;
import com.petra.lib.context.block.operations.AnswerOperation;
import com.petra.lib.context.repo.ContextRepo;
import com.petra.lib.context.model.Identifier;
import com.petra.lib.block.workflow.model.WorkflowBlockModel;
import com.petra.lib.block.workflow.model.WorkflowModel;
import com.petra.lib.block.workflow.orchestrator.ClientBlock;
import com.petra.lib.block.workflow.orchestrator.Orchestrator;
import com.petra.lib.remote.Sender;
import com.petra.lib.thread.ThreadController;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.variable.VariableFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowFactory {

    public static Block createWorkflow(ThreadController threadController, WorkflowModel workflowModel,
                                       ContextRepo contextRepo, ActionWorkflowRepo actionWorkflowRepo, Sender sender,
                                       TransactionManager transactionManager, String producerServiceUrl) {

        Identifier identifier = new Identifier(workflowModel.getId(), workflowModel.getVersion());
        AnswerOperation answerOperation = new AnswerOperation(sender, transactionManager, contextRepo);


        Map<Identifier, ClientBlock> blockMap = new HashMap<>();
//        WorkflowSignal startSignal = createSignal(workflowModel.getSignals(), workflowModel.getBlocks(), workflowModel.getStartSignal().getId(), workflowModel.getStartSignal().getVersion(),
//                producerServiceUrl, sender, threadController, actionRepo, identifier, actionWorkflowRepo, transactionManager, blockMap);
        ClientBlock startBlock = createBlock(workflowModel.getBlocks(), workflowModel.getProducerServiceUrl(), workflowModel.getStartBlock().getId(),
                workflowModel.getStartBlock().getVersion(), sender, actionWorkflowRepo, transactionManager, threadController,
                contextRepo, identifier, blockMap);
        Orchestrator orchestrator = new Orchestrator(startBlock, blockMap);

        return new Workflow(
                threadController,
                identifier,
                contextRepo,
                actionWorkflowRepo,
//                VariableFactory.createVariableManager(workflowModel.getVariableModel(),
//                        sender, workflowModel.getProducerServiceUrl(), threadController),
                answerOperation,
                transactionManager,
                sender,
                orchestrator
        );
    }

    private static ClientBlock createBlock(List<WorkflowBlockModel> workflowBlockModels,
                                           String producerServiceUrl, Long nextBlockId, String nextBlockVersion, Sender sender,
                                           ActionWorkflowRepo actionWorkflowRepo, TransactionManager transactionManager, ThreadController threadController,
                                           ContextRepo contextRepo, Identifier workflowId, Map<Identifier, ClientBlock> blockMap) {

        WorkflowBlockModel blockModel = null;
        for (WorkflowBlockModel workflowBlockModel : workflowBlockModels) {
            if (workflowBlockModel.getId().equals(nextBlockId) && workflowBlockModel.getVersion().equals(nextBlockVersion)) {
                blockModel = workflowBlockModel;
                break;
            }
        }

        if (blockModel == null) throw new NullPointerException("Block not found ");

        Identifier id = new Identifier(nextBlockId, nextBlockVersion);
        ClientBlock clientBlock = new ClientBlock(
                id,
                producerServiceUrl,
                createBlock(workflowBlockModels, producerServiceUrl,
                        blockModel.getNextBlock().getId(), blockModel.getNextBlock().getVersion(),
                        sender, actionWorkflowRepo, transactionManager, threadController, contextRepo,
                        workflowId, blockMap),
                blockModel.getName(),
                sender,
                blockModel.getConsumerServiceURL(),
                actionWorkflowRepo,
                transactionManager,
                VariableFactory.createVariableManager(blockModel.getVariableModel(), sender, producerServiceUrl, threadController, id),
                workflowId);
        blockMap.put(id, clientBlock);
        return clientBlock;
    }

//    private static WorkflowSignal createSignal(List<WorkflowSignalModel> workflowSignalModels, List<WorkflowBlockModel> workflowBlockModels,
//                                               Long nextSignalId, String nextSignalVersion, String producerServiceUrl, Sender sender,
//                                               ThreadController threadController, ActionRepo actionRepo,
//                                               Identifier workflowId, ActionWorkflowRepo actionWorkflowRepo, TransactionManager transactionManager,
//                                               Map<Identifier, ClientBlock> blockMap) {
//
//        WorkflowSignalModel currentSignal = null;
//        for (WorkflowSignalModel workflowSignalModel : workflowSignalModels) {
//            if (workflowSignalModel.getId().equals(nextSignalId) && workflowSignalModel.getVersion().equals(nextSignalVersion)) {
//                currentSignal = workflowSignalModel;
//                break;
//            }
//        }
//
//        if (currentSignal == null) throw new NullPointerException("Signal not found");
//
//        ClientBlock nextBlock = null;
//        if (currentSignal.getRequestingBlock() != null) {
//            nextBlock = createBlock(workflowSignalModels, workflowBlockModels, producerServiceUrl,
//                    currentSignal.getRequestingBlock().getId(), currentSignal.getRequestingBlock().getVersion(),
//                    sender, actionWorkflowRepo, transactionManager, threadController, actionRepo, workflowId, blockMap);
//        }
//
//        return new WorkflowSignal(
//                new Identifier(nextSignalId, nextSignalVersion),
//                workflowId,
//                producerServiceUrl,
//                nextBlock,
//                VariableFactory.createVariableManager(currentSignal.getVariableModel(), sender, producerServiceUrl, threadController),
//                actionRepo
//        );
//
//
//    }
}
