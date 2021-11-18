package io.je.Monitor.zmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.je.Monitor.zmq.MonitoringSubscriber;
import io.je.Monitor.config.MonitorProperties;
import io.siothconfig.SIOTHConfigUtility;

@Component
public class JEMonitorSubscriber {


	
    private static MonitoringSubscriber subscriber;
    
    @Autowired
    MonitorProperties monitorProperties;

    
    
    
    public void initSubscriber() {
        //"tcp://"+ SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(), SIOTHConfigUtility.getSiothConfig().getDataModelPORTS().getDmService_PubAddress(), topic
        //Subscriber subscriber = new Subscriber(/*"tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode()*/"tcp://192.168.4.128", 12345, "JEMonitorTopic");
        subscriber = new MonitoringSubscriber("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(), monitorProperties.getMonitoringPort(), "JEMonitorTopic");
    	Thread thread = new Thread(subscriber);
        subscriber.setListening(true);
        thread.start();
    }

	public JEMonitorSubscriber() {
		super();
		// TODO Auto-generated constructor stub
	}

}
