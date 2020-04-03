package info.novatec.micronaut.camunda.bpm.feature;

import io.micronaut.context.annotation.ConfigurationProperties;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@ConfigurationProperties("camunda.bpm")
public class Configuration {

    @NotNull
    private Database database = new Database();

    Database getDatabase() {
        return database;
    }

    @ConfigurationProperties("database")
    class Database {

        @NotBlank
        private String schemaUpdate;

        //@Bindable(defaultValue = "true")
        String getSchemaUpdate() {
            if (schemaUpdate == null){
                schemaUpdate = "true";
            }
            return schemaUpdate;
        }
    }
}