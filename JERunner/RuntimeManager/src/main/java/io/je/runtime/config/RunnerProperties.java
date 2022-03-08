package io.je.runtime.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import io.je.utilities.config.JEProperties;
import io.siothconfig.*;

@ConfigurationProperties
@Configuration("RunnerProperties")
@PropertySource(SIOTHConfigurationConstants.APPLICATION_PROPERTIES_PATH)
public class RunnerProperties extends JEProperties{

}
