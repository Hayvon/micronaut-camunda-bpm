package info.novatec.micronaut.camunda.bpm.feature;

import io.micronaut.aop.Introduction;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Type;
import io.micronaut.core.bind.annotation.Bindable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ConfigurationProperties("camunda.bpm")
public interface Configuration {

    @NotNull
    Database getDatabase();

   @ConfigurationProperties("database")
    interface Database {

        @NotBlank
        @Bindable(defaultValue = "true")
        String getSchemaUpdate();
    }
}
