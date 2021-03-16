package io.je.utilities.execution;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

public class Executioner {

    public static void triggerEvent(String projectId, String eventId) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
     
    	JERunnerAPIHandler.triggerEvent(eventId, projectId);
    	
    /*	Runnable runnable =  () -> {
			try {
				JERunnerAPIHandler.triggerEvent(eventId, projectId);
				
			} catch (JERunnerErrorException | IOException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
				JELogger.error(Executioner.class, "failed to trigger event");
			}
		};
	*/	
		
		
    }

    public static void executeScript(String name) {
        try {
            Class<?> clazz = Class.forName("classes." + name);
            Method method
                    = clazz.getDeclaredMethods()[0];
            method.invoke(null);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
