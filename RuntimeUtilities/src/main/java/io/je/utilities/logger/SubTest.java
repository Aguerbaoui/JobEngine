package io.je.utilities.logger;

import io.je.utilities.beans.JEData;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.zmq.ZMQSubscriber;

import java.util.Arrays;

public class SubTest extends ZMQSubscriber {


	public SubTest(String url, int subPort, String topic) {
		super(url, subPort, topic);
	}

	@Override
	public void run() {
		while(true)
    	{
   		 String data = null;
   		 try {
   			data = this.getSubSocket().recvStr();
   			System.out.println(data);
   		 }catch (Exception e) {
			e.printStackTrace();
			continue;
		}

             
             try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
		
	}


}
