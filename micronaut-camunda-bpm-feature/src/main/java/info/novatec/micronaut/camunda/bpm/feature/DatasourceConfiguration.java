package info.novatec.micronaut.camunda.bpm.feature;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.bind.annotation.Bindable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("datasources.default")
public class DatasourceConfiguration {
    @NotBlank
    private String url;
    @NotBlank
    private String username;
    @NotNull
    private String password;
    @NotBlank
    private String driverClassName;

    //@Bindable(defaultValue = "jdbc:h2:mem:micronaut-db;DB_CLOSE_DELAY=1000")
    String getUrl() {
        if (url == null){
            url = "jdbc:h2:mem:micronaut-db;DB_CLOSE_DELAY=1000";
        }
        return url;
    }

    //@Bindable(defaultValue = "sa")
    String getUsername() {
        if (username == null){
            username = "sa";
        }
        return username;
    }

    //@Bindable(defaultValue = "")
    String getPassword() {
        if (password== null){
            password = "";
        }
        return password;
    }

    //@Bindable(defaultValue = "org.h2.Driver")
    String getDriverClassName() {
        if (driverClassName == null){
            driverClassName = "org.h2.Driver";
        }
        return driverClassName;
    }
}