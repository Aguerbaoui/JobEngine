package io.je.project.beans;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.je.project.services.ClassService;
import io.je.utilities.logger.JELogger;

@Component
public class JEInitializingBean implements InitializingBean {


	@Autowired
	ClassService classService;

    @Override
    public void afterPropertiesSet() throws Exception {
    	
    	//load existing classes from database
        try
        {
        	classService.loadAllClasses();
        }
        catch(Exception e)
        {
        	//TODO: remove hard-coded msg
        	JELogger.error(getClass(), "Failed to load classes " + e.getMessage());
        }
    }
}