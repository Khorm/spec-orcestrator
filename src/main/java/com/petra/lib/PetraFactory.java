package com.petra.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petra.lib.action.Action;
import com.petra.lib.action.ActionFactory;
import com.petra.lib.action.annotation.ActionHandler;
import com.petra.lib.action.repo.ActionRepo;
import com.petra.lib.action.repo.ActionRepoFactory;
import com.petra.lib.block.action.executor.handler.UserActionHandler;
import com.petra.lib.query.ThreadQuery;
import com.petra.lib.query.ThreadQueryFactory;
import com.petra.lib.remote.request.block.BlockRequest;
import com.petra.lib.remote.request.block.BlockRequestFactory;
import com.petra.lib.remote.request.source.SourceRequest;
import com.petra.lib.remote.request.source.SourceRequestFactory;
import com.petra.lib.remote.response.block.BlockResponse;
import com.petra.lib.remote.response.block.BlockResponseFactory;
import com.petra.lib.remote.response.source.SourceResponse;
import com.petra.lib.remote.response.source.SourceResponseFactory;
import com.petra.lib.remote.thread.RemoteThreadManager;
import com.petra.lib.source.Source;
import com.petra.lib.source.SourceFactory;
import com.petra.lib.source.annotation.SourceHandler;
import com.petra.lib.source.user.UserSourceHandler;
import com.petra.lib.transaction.TransactionManager;
import com.petra.lib.transaction.TransactionManagerFactory;
import com.petra.lib.workflow.Workflow;
import com.petra.lib.workflow.WorkflowFactoty;
import com.petra.lib.workflow.block.BlockBuilderModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Компонент соединяющий петру и спринг
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public final class PetraFactory implements ApplicationContextAware {

    final DataSource dataSource;
//    final PlatformTransactionManager platformTransactionManager;

    @Value("${petra.kafka.servers}")
    String kafkaServers;



    final PetraProps petraProps;

    final ConfigurableApplicationContext context;
    final EntityManagerFactory entityManagerFactory;
    final JpaTransactionManager jpaTransactionManager;

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {


        Resource config = applicationContext.getResource("classpath:petra_config.json");
        String configJson = null;
        try {
            configJson = new String(Files.readAllBytes(config.getFile().toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        PetraBuildModel configModel;
        try {
            configModel = objectMapper.readValue(configJson, PetraBuildModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ThreadQuery threadQuery = ThreadQueryFactory.createThreadQuery(petraProps.getThreadsCount());
        SourceResponse sourceResponse = SourceResponseFactory.createSourceResponse();
        RemoteThreadManager remoteThreadManager = new RemoteThreadManager();
        TransactionManager transactionManager = TransactionManagerFactory.createTransactionManager(jpaTransactionManager);

        Map<String, Object> sourceHandlersBeans = applicationContext.getBeansWithAnnotation(SourceHandler.class);
        Map<String, UserSourceHandler> sourceHandlers = sourceHandlersBeans.values().stream()
                .collect(Collectors.toMap(valForKey -> valForKey.getClass().getAnnotation(SourceHandler.class).name(),
                        o -> (UserSourceHandler) o));
        Collection<Source> sources = SourceFactory.createSources(configModel.getSourceBuildModels(),
                sourceHandlers,
                threadQuery,
                sourceResponse,
                remoteThreadManager,
                transactionManager
                );


        BlockResponse blockResponse = BlockResponseFactory.createBlockResponse();
        ActionRepo actionRepo = ActionRepoFactory.createActionRepo(transactionManager);
        Map<String, Object> actionHandlersBeans = applicationContext.getBeansWithAnnotation(ActionHandler.class);
        Map<String, UserActionHandler> actionHandlers = actionHandlersBeans.values().stream()
                .collect(Collectors.toMap(valForKey -> valForKey.getClass().getAnnotation(ActionHandler.class).name(),
                        o -> (UserActionHandler) o));
        Collection<Action> actions = ActionFactory.createActions(
                configModel.getActionBuildModels(),
                actionHandlers,
                actionRepo,
                threadQuery,
                transactionManager,
                blockResponse,
                remoteThreadManager
        );


        SourceRequest sourceRequest = SourceRequestFactory.createSourceRequest();
        BlockRequest blockRequest = BlockRequestFactory.createBlockRequest();
        /**
         * Подменяет имя сервисов на имя указанное в параметрах сервиса
         */
        for (BlockBuilderModel blockBuilderModel : configModel.getBlockBuilderModels()){
            for(ServiceModel serviceModel : petraProps.getServices()){
                if (blockBuilderModel.getBlockExecutorBuildModel().getConsumerServiceName().equals(serviceModel.getName())){
                    blockBuilderModel.getBlockExecutorBuildModel().setConsumerServiceName(serviceModel.getName());
                }
                if (blockBuilderModel.getBlockExecutorBuildModel().getProducerServiceName().equals(serviceModel.getName())){
                    blockBuilderModel.getBlockExecutorBuildModel().setProducerServiceName(serviceModel.getName());
                }
            }
        }
        Collection<Workflow> workflows = WorkflowFactoty.createWorkflows(
                transactionManager,
                configModel.getSignalBuildModels(),
                blockResponse,
                remoteThreadManager,
                configModel.getWorkflowBuildModels(),
                configModel.getBlockBuilderModels(),
                sourceRequest,
                blockRequest
        );

        actions.forEach(Action::start);
        workflows.forEach(Workflow::start);




//        dsasd
//
//        UserHandlerExecutor userHandlerExecutor = new UserHandlerExecutor()

//        ThreadManager.setPoolSize(threadsCount);
//        Map<String, Object> sourceHandlersBeans = applicationContext.getBeansWithAnnotation(WorkflowHandler.class);
//
//        Map<String, Object> workflowHandlers = sourceHandlersBeans.values().stream()
//                        .collect(Collectors.toMap( valForKey -> {
//                            return valForKey.getClass().getAnnotation(WorkflowHandler.class).name();
//                        }, Function.identity()));
//
//
//        JobRepository jobRepository = new JobRepository(jpaTransactionManager);
//        try {
//            Resource config = applicationContext.getResource("classpath:petra_config.json");
//            String configJson = new String(Files.readAllBytes(config.getFile().toPath()));
//            ObjectMapper objectMapper = new ObjectMapper();
//            ConfigModel configModel = objectMapper.readValue(configJson, ConfigModel.class);
//            BlockFactory blockFactory = new BlockFactory();
//
//            Collection<JobStaticManager> sourceExecutors = configModel.getSources().stream()
//                    .map(sourceModel -> blockFactory.createSource(sourceModel,
//                            (UserHandler) workflowHandlers.get(sourceModel.getName()),
//                            jobRepository,
//                            jpaTransactionManager,
//                            sourceModel.getExecutionSignal().getPath(),
//                            context.getBeanFactory(),
//                            entityManagerFactory
//                            )).collect(Collectors.toList());
//            sourceExecutors.forEach(JobStaticManager::start);
//
//            Collection<JobStaticManager> actionExecutors = configModel.getActions().stream()
//                    .map(actionModel -> {
//                        return blockFactory.createAction(
//                                actionModel,
//                                (UserHandler) workflowHandlers.get(actionModel.getName()),
//                                jobRepository,
//                                jpaTransactionManager,
//                                actionModel.getExecutionSignal().getPath(),
//                                context.getBeanFactory(),
//                                kafkaServers
//                        );
//                    }).collect(Collectors.toList());
//            actionExecutors.forEach(JobStaticManager::start);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
