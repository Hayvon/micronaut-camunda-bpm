package info.novatec.micronaut.camunda.bpm.feature;


import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.convert.format.MapFormat;

import javax.inject.Singleton;
import java.util.Map;

@ConfigurationProperties("genericProperties")
public  class GenericProperties {

        @MapFormat
        Map<String, String> properties;

       /* public Map<String, String> getProperties() {
            return properties;
        }*/

       public String getValue(String key) {
            return properties.get(key);
        }

}
