package io.je.Monitor.zmq;

import static io.je.utilities.constants.JEMessages.STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import io.je.Monitor.service.WebSocketService;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQSecurity;
import utils.zmq.ZMQSubscriber;

@Component
public class MonitoringSubscriber extends ZMQSubscriber{

	  @Autowired
	    WebSocketService service;
	
	  @Autowired
	public MonitoringSubscriber(String url, int subPort, String topic) {
		super(url, subPort, topic);
		// TODO Auto-generated constructor stub
	}
	

	
	@Override
    public ZMQ.Socket getSubSocket() {
        if(subSocket == null) {
            try {
                this.subSocket = this.context.createSocket(SocketType.SUB);
                this.subSocket.setReceiveTimeOut(1000);
                this.subSocket.bind(url+":"+subPort);
                this.subSocket.subscribe(topic.getBytes());
               if(ZMQSecurity.isSecure())
               {
            	   subSocket.setCurveServerKey(ZMQSecurity.getServerPair().publicKey.getBytes());
                   subSocket.setCurveSecretKey(ZMQSecurity.getServerPair().secretKey.getBytes());
           		   subSocket.setCurvePublicKey(ZMQSecurity.getServerPair().publicKey.getBytes());
               }

            } catch (Exception e) {
            	System.out.println( e.getMessage());
                this.subSocket = null;
            }
        }
        return subSocket;
    }
	
	@Override
	public void run() {
		 JELogger.control(STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE, LogCategory.MONITOR, null,
	                LogSubModule.JEMONITOR, null);
	        while (listening) {
	        	String data = null;

	            try {
	                data = this.getSubSocket().recvStr();
	                if(data != null  && !data.equals(topic) && !data.startsWith(topic)) {
	                    JELogger.debug(data);
	                	service.sendUpdates(data);
	                
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	                continue;
	            }
	       
	        try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        }
		
	}

}
