package io.je.Monitor.zmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.je.Monitor.zmq.MonitoringSubscriber;
import io.je.Monitor.config.MonitorProperties;
import io.siothconfig.SIOTHConfigUtility;

@Component
public class JEMonitorSubscriber {


	@Autowired
	MonitoringSubscriber subscriber;
	
    @Autowired
    MonitorProperties monitorProperties;

    
    
    
    public void initSubscriber() {
        subscriber.setConfig("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(), monitorProperties.getMonitoringPort(), "JEMonitorTopic");
    	Thread thread = new Thread(subscriber);
        subscriber.setListening(true);
        thread.start();
    }

	public JEMonitorSubscriber() {
		super();
		// TODO Auto-generated constructor stub
	}

}
