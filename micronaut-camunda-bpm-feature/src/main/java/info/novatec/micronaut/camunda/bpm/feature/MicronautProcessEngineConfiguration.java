package info.novatec.micronaut.camunda.bpm.feature;

import io.micronaut.cli.io.support.PathMatchingResourcePatternResolver;
import io.micronaut.cli.io.support.Resource;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Factory
public class MicronautProcessEngineConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MicronautProcessEngineConfiguration.class);
    public static final String MICRONAUT_AUTO_DEPLOYMENT_NAME = "MicronautAutoDeployment";
    public static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    private int diagramcount;
    private String[] diagrams;

    private final ApplicationContext applicationContext;

    private final Configuration configuration;

    private final DatasourceConfiguration datasourceConfiguration;

    public MicronautProcessEngineConfiguration(ApplicationContext applicationContext, Configuration configuration, DatasourceConfiguration datasourceConfiguration) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
        this.datasourceConfiguration = datasourceConfiguration;
    }

    /**
     * The {@link ProcessEngine} is started with the application start so that the task scheduler is started immediately.
     *
     * @return the initialized {@link ProcessEngine} in the application context.
     * @throws IOException if a resource, i.e. a model, cannot be loaded.
     */
    @Context
    public ProcessEngine processEngine() throws IOException {
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(configuration.getDatabase().getSchemaUpdate())
                .setJdbcUrl(datasourceConfiguration.getUrl())
                .setJdbcUsername(datasourceConfiguration.getUsername())
                .setJdbcPassword(datasourceConfiguration.getPassword())
                .setJdbcDriver(datasourceConfiguration.getDriverClassName())
                .setJobExecutorActivate(true);

        ((ProcessEngineConfigurationImpl) processEngineConfiguration).setExpressionManager(new MicronautExpressionManager(new ApplicationContextElResolver(applicationContext)));

        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        log.info("Successfully created process engine which is connected to database {}", datasourceConfiguration.getUrl());

        deployProcessModels(processEngine);

        return processEngine;
    }

    private void deployProcessModels(ProcessEngine processEngine) throws IOException {
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
        StringBuilder textBuilder = new StringBuilder();

        for(Resource allDiagramsFile : resourceLoader.getResources(CLASSPATH_ALL_URL_PREFIX +"bpm/diagrams.txt")){
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (allDiagramsFile.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }
            String stringinput = textBuilder.toString();
            diagrams = stringinput.split("\\r?\\n");
            diagramcount = diagrams.length;
        }

        for (int i = 0; i < diagramcount; i++) {
            for (Resource resource : resourceLoader.getResources(CLASSPATH_ALL_URL_PREFIX +"bpm/" + diagrams[i])) {
                log.info("Deploying model from classpath: {}", resource.getFilename());

                processEngine.getRepositoryService().createDeployment()
                        .name(MICRONAUT_AUTO_DEPLOYMENT_NAME)
                        .addInputStream(resource.getFilename(), resource.getInputStream())
                        //.enableDuplicateFiltering(true)
                        .deploy();
            }
        }
    }

    /**
     * Creates a bean for the {@link RuntimeService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link RuntimeService}
     */
    @Singleton
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    /**
     * Creates a bean for the {@link RepositoryService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link RepositoryService}
     */
    @Singleton
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    /**
     * Creates a bean for the {@link ManagementService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link ManagementService}
     */
    @Singleton
    public ManagementService managementService(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }

    /**
     * Creates a bean for the {@link AuthorizationService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link AuthorizationService}
     */
    @Singleton
    public AuthorizationService authorizationService(ProcessEngine processEngine) {
        return processEngine.getAuthorizationService();
    }

    /**
     * Creates a bean for the {@link CaseService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link CaseService}
     */
    @Singleton
    public CaseService caseService(ProcessEngine processEngine) {
        return processEngine.getCaseService();
    }

    /**
     * Creates a bean for the {@link DecisionService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link DecisionService}
     */
    @Singleton
    public DecisionService decisionService(ProcessEngine processEngine) {
        return processEngine.getDecisionService();
    }

    /**
     * Creates a bean for the {@link ExternalTaskService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link ExternalTaskService}
     */
    @Singleton
    public ExternalTaskService externalTaskService(ProcessEngine processEngine) {
        return processEngine.getExternalTaskService();
    }

    /**
     * Creates a bean for the {@link FilterService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link FilterService}
     */
    @Singleton
    public FilterService filterService(ProcessEngine processEngine) {
        return processEngine.getFilterService();
    }

    /**
     * Creates a bean for the {@link FormService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link FormService}
     */
    @Singleton
    public FormService formService(ProcessEngine processEngine) {
        return processEngine.getFormService();
    }

    /**
     * Creates a bean for the {@link TaskService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link TaskService}
     */
    @Singleton
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    /**
     * Creates a bean for the {@link HistoryService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link HistoryService}
     */
    @Singleton
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    /**
     * Creates a bean for the {@link IdentityService} in the application context which can be injected if needed.
     * @param processEngine the {@link ProcessEngine}
     * @return the {@link IdentityService}
     */
    @Singleton
    public IdentityService identityService (ProcessEngine processEngine) {
        return processEngine.getIdentityService();
    }

}
