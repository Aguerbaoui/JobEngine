package io.je.runtime.data;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEData;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.zmq.ZMQSubscriber;

import java.util.Arrays;

public class ZMQAgent extends ZMQSubscriber {


	public ZMQAgent(String url, int subPort, String topic) {
		super(url, subPort, topic);
	}

	@Override
	public void run() {
		while(listening)
    	{
   		 String data = null;
   		 try {
   			data = this.getSubSocket().recvStr();
   		 }catch (Exception e) {
			e.printStackTrace();
			continue;
		}

             try {
            	 if( data !=null && !data.equals(topic) && !data.startsWith(topic))
				{ ;
					JELogger.debug(JEMessages.DATA_RECEIVED + data,  LogCategory.RUNTIME,
							null, LogSubModule.RULE, null);
            		 RuntimeDispatcher.injectData(new JEData(this.topic, data));
				}
			} catch (Exception e) {
				 JELogger.error(JEMessages.UKNOWN_ERROR + Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME, null,
						 LogSubModule.JERUNNER, null);
			}
             
             try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				 JELogger.error(JEMessages.THREAD_INTERRUPTED, LogCategory.RUNTIME, null,
						 LogSubModule.JERUNNER, null);
			}
    	}
		
	}


}
