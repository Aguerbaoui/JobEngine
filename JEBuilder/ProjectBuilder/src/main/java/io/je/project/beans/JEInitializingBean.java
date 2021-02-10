package io.je.project.beans;

import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.exceptions.JERunnerErrorException;
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
	ClassService classService;

    @Override
    public void afterPropertiesSet() {
    	
    	//load existing classes from database
        try {
            JELogger.trace(getClass(), " Loading classes from data definition ");
            classService.loadAllClasses();
        } catch (DataDefinitionUnreachableException | JERunnerErrorException | AddClassException | ClassLoadException | IOException | InterruptedException | ExecutionException e) {
           JELogger.error(getClass(), e.getMessage());
        }

    }
}