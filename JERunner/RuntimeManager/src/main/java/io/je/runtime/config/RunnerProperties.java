package io.je.runtime.config;

import io.je.utilities.config.JEProperties;
import io.siothconfig.SIOTHConfigurationConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties
@Configuration("RunnerProperties")
@PropertySource(SIOTHConfigurationConstants.APPLICATION_PROPERTIES_PATH)
public class RunnerProperties extends JEProperties {

}
