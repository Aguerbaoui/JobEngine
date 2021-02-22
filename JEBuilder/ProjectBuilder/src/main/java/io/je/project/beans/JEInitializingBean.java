package io.je.project.beans;

import io.je.project.services.ProjectService;
import io.je.utilities.exceptions.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.je.project.services.ClassService;
import io.je.utilities.logger.JELogger;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class JEInitializingBean implements InitializingBean {


	@Autowired
    ProjectService projectService;
    @Override
    public void afterPropertiesSet() {
    	
    	//load existing classes from database
        try {
            projectService.initialize();
        } catch (DataDefinitionUnreachableException | JERunnerErrorException | AddClassException | ClassLoadException | IOException | InterruptedException | ExecutionException  | ProjectRunException | RuleNotFoundException | RuleBuildFailedException  e) {
           JELogger.error(getClass(), e.getMessage());
        }

    }
}