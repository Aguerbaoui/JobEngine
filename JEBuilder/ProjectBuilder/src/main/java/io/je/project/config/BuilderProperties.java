package io.je.project.config;

import io.je.utilities.config.JEProperties;
import io.siothconfig.SIOTHConfigurationConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;

/**
 * jobengine.properties configuration class
 */
@ConfigurationProperties
@Configuration("BuilderProperties")
@PropertySource(SIOTHConfigurationConstants.APPLICATION_PROPERTIES_PATH)
@Lazy
public class BuilderProperties extends JEProperties {

}
