package io.je.runtime.events;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.beans.JEBlockMessage;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.beans.JEMessage;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogLevel;
import io.je.utilities.logger.LogMessage;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.logger.ZMQLogPublisher;

public class EventTimeoutRunnable implements Runnable {
	
			JEEvent event;
		    private static 	ObjectMapper objectMapper = new ObjectMapper();
		    
		    


		public EventTimeoutRunnable(JEEvent event) {
				super();
				this.event = event;
			}


		
		
		@Override
		public void run() {
			try {
				Thread.sleep(event.getTimeout());
				synchronized(event)
				{
						event.setTriggered(false);
						//TODO: I think we should remove the call to the builder, and runtime should read those values elsewhere.
		        	/*	try {
							//JEBuilderApiHandler.untriggerEvent(event.getJobEngineElementID(), event.getJobEngineProjectID());
						} catch (JERunnerErrorException | ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		*/
		        		 LogMessage msg = new LogMessage(LogLevel.DEBUG,  event.getName() + " timed out ",  LocalDateTime.now().toString(), "JobEngine",  event.getJobEngineProjectID(),
	                				event.getJobEngineElementID(), LogSubModule.EVENT, "APP", null, "Log", "") ;
	                	   ZMQLogPublisher.publish(msg);
	                  /*  JEMessage message = new JEMessage();
	                    message.setExecutionTime(LocalDateTime.now().toString());
	                    message.setType("BlockMessage");
	                    JEBlockMessage blockMessage = new JEBlockMessage("Application",  event.getName() +" was untriggered");
	                    message.addBlockMessage(blockMessage);
	                    
	                    try {
							//JELogger.trace(objectMapper.writeValueAsString(message), LogCategory.RUNTIME, event.getJobEngineElementID(), LogSubModule.EVENT, event.getJobEngineElementID());
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
					}
				

			} catch (InterruptedException e) {
				
			}

		}

}
