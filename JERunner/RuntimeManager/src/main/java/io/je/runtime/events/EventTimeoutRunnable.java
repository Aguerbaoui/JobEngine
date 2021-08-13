package io.je.runtime.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.beans.JEEvent;
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
						event.untrigger();
	                 
					}
				

			} catch (InterruptedException e) {
				
			}

		}

}
