package info.novatec.micronaut.camunda.bpm.feature;

import io.micronaut.context.env.ActiveEnvironment;
import io.micronaut.context.env.PropertySource;
import io.micronaut.context.env.PropertySourceLoader;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.order.Ordered;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class GenericPropertiesLoader implements PropertySourceLoader, Ordered {

    /**
     * Position for the system property source loader in the chain.
     */
    private static final int POSITION = Ordered.LOWEST_PRECEDENCE;


    HashMap<String, String> test = new HashMap<>();


    //Inject config


    @Override
    public int getOrder() {
        return POSITION;
    }

    @Override
    public Optional<PropertySource> load(String resourceName, ResourceLoader resourceLoader) {
        System.out.println("Test2");
        return Optional.of(
                PropertySource.of(
                        "test",
                        Collections.unmodifiableMap(test)));
    }


    @Override
    public Optional<PropertySource> loadEnv(String resourceName, ResourceLoader resourceLoader, ActiveEnvironment activeEnvironment) {
        return Optional.empty();
    }

    @Override
    public Map<String, Object> read(String name, InputStream input) {
        return new HashMap<>();
    }
}
