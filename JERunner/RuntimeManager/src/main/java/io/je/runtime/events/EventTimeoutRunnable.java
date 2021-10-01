package io.je.runtime.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.beans.JEEvent;
import io.je.utilities.log.ZMQLogPublisher;
import utils.log.LogLevel;
import utils.log.LogMessage;
import utils.log.LogSubModule;

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
