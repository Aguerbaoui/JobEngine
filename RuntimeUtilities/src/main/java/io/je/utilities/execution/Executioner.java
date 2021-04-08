package io.je.utilities.execution;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.monitoring.MessageModel;

import io.je.utilities.zmq.ZMQRequester;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

//import org.influxdb.InfluxDB;
//import org.influxdb.InfluxDBFactory;

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
    
    public static void writeMonitoringMessageToInfluxDb(MessageModel messageModel) {
    	//InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "io", "io.123");

    }
    



    public static void writeToInstance(String instanceId, String attributeName, String value)
    {
    	String request = generateRequest(instanceId,attributeName,value);
    	
    	try
    	{
    		new Thread(new Runnable() {
    	
			@Override
			public void run() {
	    		JELogger.debug(JEMessages.SENDING_REQUEST_TO_DATA_MODEL + " : " + request );
				ZMQRequester requester = new ZMQRequester(JEConfiguration.getDataManagerURL(),JEConfiguration.getRequestPort() );
				String response = requester.sendRequest(request);
				if(response==null)
				{
					JELogger.error(getClass(), JEMessages.NO_RESPONSE_FROM_DATA_MODEL);
				}else {
					JELogger.info("Data Model Returned" + response);
				}
	    		

			}
		}).start();
    	}catch (Exception e) {
			// TODO: handle exception
		}
    
    }





/*
 * generate data model write request 
 */
    private static String generateRequest(String instanceId, String attributeName, String attributeNewValue) {
    	 String req = "{\r\n"
			 		+ "   \"InstanceId\":\"" +instanceId+"\",\r\n"
			 		+ "   \"Attributes\":[\r\n"
			 		+ "      {\r\n"
			 		+ "         \"Name\":\""+attributeName+"\",\r\n"
			 		+ "         \"Value\":\""+attributeNewValue+"\"\r\n"
			 		+ "      }\r\n"
			 		+ "   ]\r\n"
			 		+ "}";
		return req;
	}

    public static void executeScript(String name) {
    	new Thread(() -> {
			try {
				Class<?> clazz = Class.forName("classes." + name);
				Method method
						= clazz.getDeclaredMethods()[0];
				method.invoke(null);
			} catch (Exception e) {
				JELogger.info("Failed to execute script in script task\n");
				JELogger.info(Arrays.toString(e.getStackTrace()));
				e.printStackTrace();
			}
		}).start();
    }



}
