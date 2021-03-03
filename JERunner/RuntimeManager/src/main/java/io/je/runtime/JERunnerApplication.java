package io.je.runtime;

import java.util.Collections;

import io.je.project.config.ConfigurationConstants;
import io.je.utilities.apis.JEBuilderApiHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import io.je.utilities.logger.JELogger;

import javax.annotation.PreDestroy;
import io.je.runtime.config.ConfigurationConstants;

@SpringBootApplication
@EnableAutoConfiguration
@PropertySource(ConfigurationConstants.APPLICATION_PROPERTIES_PATH)
public class JERunnerApplication {

	
    public static void main(String[] args) {
        SpringApplication.run(JERunnerApplication.class, args);
    }

    @PreDestroy
    public void onDestroy() throws Exception {
    	JEBuilderApiHandler.requestUpdateFromBuilder();
    }
}



