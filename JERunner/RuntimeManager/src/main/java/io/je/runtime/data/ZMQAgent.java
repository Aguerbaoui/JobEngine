package io.je.runtime.data;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEData;
import io.je.utilities.beans.JEMessages;
import io.je.utilities.logger.JELogger;
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
				{
                     JELogger.info(ZMQAgent.class, JEMessages.DATA_RECEIVED + data);
            		 RuntimeDispatcher.injectData(new JEData(this.topic, data));
				}
			} catch (Exception e) {
				JELogger.error(ZMQAgent.class, Arrays.toString(e.getStackTrace()));
			}
             
             try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
                 JELogger.error(ZMQAgent.class,JEMessages.THREAD_INTERRUPTED );
			}
    	}
		
	}


}
