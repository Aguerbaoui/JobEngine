package io.je.project.beans;

import io.je.project.services.ProjectService;
import io.je.utilities.exceptions.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.je.project.services.ClassService;
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

    @Override
    public void afterPropertiesSet() {
    	
        try {
            JELogger.initBuilderLogger();
        	configService.init();
        } catch (DataDefinitionUnreachableException | JERunnerErrorException | AddClassException | ClassLoadException | IOException | InterruptedException | ExecutionException   e) {
           JELogger.error(getClass(), e.getMessage());
        }

    }
}