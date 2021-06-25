package io.je.project.beans;

import io.je.project.services.ProjectService;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.je.project.config.BuilderProperties;
import io.je.project.services.ConfigurationService;
import io.je.utilities.logger.JELogger;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class JEBuilderInitializingBean implements InitializingBean {


	@Autowired
    ProjectService projectService;
	
	@Autowired 
	ConfigurationService configService;
	
	@Autowired
	BuilderProperties builderProperties;
    @Override
    public void afterPropertiesSet() {
        try {
            JELogger.initBuilderLogger(builderProperties.getJeBuilderLogPath(),builderProperties.getJeBuilderLogLevel());
            JELogger.trace(JEMessages.LOGGER_INITIALIZED);
        	configService.init();
        } catch (  JERunnerErrorException   | IOException | InterruptedException | ExecutionException | ProjectNotFoundException | ConfigException   e) {
           JELogger.error(getClass(), e.getMessage());
        }

    }
}