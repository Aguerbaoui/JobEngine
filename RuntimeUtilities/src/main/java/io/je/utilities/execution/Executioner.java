package io.je.utilities.execution;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.zmq.ZMQRequester;

import java.io.IOException;
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



    public static void writeToDataModel(String request)
    {
    	
    	try
    	{
    		new Thread(new Runnable() {
    	
			@Override
			public void run() {
	    		JELogger.debug("Sending request to Data Model : " + request );
				ZMQRequester requester = new ZMQRequester(JEConfiguration.getDataManagerURL(),JEConfiguration.getRequestPort() );
				String response = requester.sendRequest(request);
				if(response==null)
				{
					JELogger.error(getClass(), "No Response from Data Model ");
				}else {
					JELogger.info("Data Model Returned" + response);
				}
	    		

			}
		}).start();
    	}catch (Exception e) {
			// TODO: handle exception
		}
    
    }






}
