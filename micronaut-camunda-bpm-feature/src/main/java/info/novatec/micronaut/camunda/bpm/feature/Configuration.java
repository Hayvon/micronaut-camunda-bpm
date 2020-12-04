package info.novatec.micronaut.camunda.bpm.feature;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.bind.annotation.Bindable;
import io.micronaut.core.convert.format.MapFormat;
import org.camunda.bpm.engine.ProcessEngineConfiguration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tobias Schäfer
 */
@ConfigurationProperties("camunda.bpm")
public interface Configuration {

    @NotBlank
    @Bindable(defaultValue = ProcessEngineConfiguration.HISTORY_AUTO)
    String getHistoryLevel();

    @NotNull
    Database getDatabase();

    @NotNull
    Telemetry getTelemetry();

    @NotNull
    GenericProperties getGenericProperties();

    @ConfigurationProperties("database")
    interface Database {

        @NotBlank
        @Bindable(defaultValue = ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
        String getSchemaUpdate();
    }

    @ConfigurationProperties("telemetry")
    interface Telemetry {

        @Bindable(defaultValue = "true")
        boolean isTelemetryReporterActivate();

        @Bindable(defaultValue = "false")
        boolean isInitializeTelemetry();
    }

    @ConfigurationProperties("genericProperties")
    interface GenericProperties {
        //TODO: https://github.com/camunda/camunda-bpm-spring-boot-starter/pull/139/commits/db34812f808381ddd2677c44d0722af159a8cb1d

        @MapFormat(transformation = MapFormat.MapTransformation.FLAT)
        Map<String, String> properties = new HashMap<>();

        public Map<String, String> getProperties();

    }

}
