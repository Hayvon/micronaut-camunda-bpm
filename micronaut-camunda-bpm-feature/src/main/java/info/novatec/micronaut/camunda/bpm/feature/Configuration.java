package info.novatec.micronaut.camunda.bpm.feature;

import io.micronaut.aop.Introduction;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Type;
import io.micronaut.core.bind.annotation.Bindable;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
