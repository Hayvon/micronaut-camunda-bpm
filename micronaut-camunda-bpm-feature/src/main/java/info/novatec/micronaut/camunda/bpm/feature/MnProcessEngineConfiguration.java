package info.novatec.micronaut.camunda.bpm.feature;

import camundajar.impl.com.google.gson.internal.Primitives;
import info.novatec.micronaut.camunda.bpm.feature.tx.MnTransactionContextFactory;
import info.novatec.micronaut.camunda.bpm.feature.tx.MnTransactionInterceptor;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.transaction.SynchronousTransactionManager;
import org.camunda.bpm.engine.ArtifactFactory;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.interceptor.*;
import org.camunda.bpm.engine.impl.jobexecutor.DefaultJobExecutor;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.camunda.bpm.engine.impl.telemetry.TelemetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;

import static io.micronaut.transaction.TransactionDefinition.Propagation.REQUIRED;
import static io.micronaut.transaction.TransactionDefinition.Propagation.REQUIRES_NEW;

/**
 * Micronaut implementation of {@link org.camunda.bpm.engine.ProcessEngineConfiguration} which is aware of transaction
 * management, i.e. the surrounding transaction will be used and {@link org.camunda.bpm.engine.delegate.JavaDelegate}s
 * are executed in a transaction allowing the persistence of data with micronaut-data.
 *
 * @author Tobias Schäfer
 * @author Lukasz Frankowski
 */
@Singleton
@Introspected
public class MnProcessEngineConfiguration extends ProcessEngineConfigurationImpl {

    private static final Logger log = LoggerFactory.getLogger(MnProcessEngineConfiguration.class);

    protected final SynchronousTransactionManager<Connection> transactionManager;

    protected final JobExecutorCustomizer jobExecutorCustomizer;

    public MnProcessEngineConfiguration(Configuration configuration, ApplicationContext applicationContext, DataSource dataSource, SynchronousTransactionManager<Connection> transactionManager, ProcessEngineConfigurationCustomizer processEngineConfigurationCustomizer, ArtifactFactory artifactFactory, JobExecutorCustomizer jobExecutorCustomizer, TelemetryRegistry telemetryRegistry) throws Exception {
        this.transactionManager = transactionManager;
        this.jobExecutorCustomizer = jobExecutorCustomizer;
        setDataSource(dataSource);
        setTransactionsExternallyManaged(true);
        setDatabaseSchemaUpdate(configuration.getDatabase().getSchemaUpdate());
        setHistory(configuration.getHistoryLevel());
        setJobExecutorActivate(true);
        setExpressionManager(new MnExpressionManager(new ApplicationContextElResolver(applicationContext)));
        setArtifactFactory(artifactFactory);

        configureTelemetry(configuration, telemetryRegistry);

        configureGenericProperties(configuration);

        processEngineConfigurationCustomizer.customize(this);
    }

    @Override
    public ProcessEngine buildProcessEngine() {
        return transactionManager.executeWrite(
            transactionStatus -> {
                log.info("Building process engine connected to {}", dataSource.getConnection().getMetaData().getURL());
                return super.buildProcessEngine();
            }
        );
    }

    @Override
    public HistoryLevel getDefaultHistoryLevel() {
        // Define default history level for history level "auto".
        return HistoryLevel.HISTORY_LEVEL_FULL;
    }

    @Override
    protected void initTransactionContextFactory() {
        if(transactionContextFactory == null) {
            transactionContextFactory = new MnTransactionContextFactory(transactionManager);
        }
    }

    @Override
    protected void initJobExecutor(){
        JobExecutor jobExecutor = new DefaultJobExecutor();
        jobExecutorCustomizer.customize(jobExecutor);
        setJobExecutor(jobExecutor);
        super.initJobExecutor();
    }

    @Override
    protected Collection< ? extends CommandInterceptor> getDefaultCommandInterceptorsTxRequired() {
        return getCommandInterceptors(false);
    }

    @Override
    protected Collection< ? extends CommandInterceptor> getDefaultCommandInterceptorsTxRequiresNew() {
        return getCommandInterceptors(true);
    }

    private List<CommandInterceptor> getCommandInterceptors(boolean requiresNew) {
        return Arrays.asList(
                new LogInterceptor(),
                new CommandCounterInterceptor(this),
                new ProcessApplicationContextInterceptor(this),
                new MnTransactionInterceptor(transactionManager, requiresNew ? REQUIRES_NEW : REQUIRED),
                new CommandContextInterceptor(commandContextFactory, this, requiresNew)
        );
    }

    private void configureTelemetry(Configuration configuration, TelemetryRegistry telemetryRegistry) {
        setTelemetryReporterActivate(configuration.getTelemetry().isTelemetryReporterActivate());
        if (configuration.getTelemetry().isInitializeTelemetry()) {
            setInitializeTelemetry(true);
        }
        setTelemetryRegistry(telemetryRegistry);
    }

    public void configureGenericProperties(Configuration configuration) throws Exception {
        final Map<String, Object> genericProperties = configuration.getGenericProperties().getProperties();
        final BeanIntrospection<MnProcessEngineConfiguration> introspection = BeanIntrospection.getIntrospection(MnProcessEngineConfiguration.class);

        for(Map.Entry<String, Object > entry : genericProperties.entrySet()){
            Optional<BeanProperty<MnProcessEngineConfiguration, Object>> property = introspection.getProperty(entry.getKey());

           if (property.isPresent()){
               if (entry.getValue().getClass().getTypeName().equals("java.lang.String")) {
                   //TODO: replace with dynamic cast
                   switch (property.get().getType().toString()) {
                       case "int":
                           property.get().set(this, Integer.valueOf(String.valueOf(entry.getValue())));
                           break;
                       case "boolean":
                           property.get().set(this, Boolean.valueOf(String.valueOf(entry.getValue())));
                           break;
                       case "long":
                           property.get().set(this, Long.valueOf(String.valueOf(entry.getValue())));
                           break;
                   }
               }else{
                   property.get().set(this, entry.getValue());
               }
            }else{
                throw new Exception("Invalid property: "  + entry.getKey());
            }
        }
    }

}
