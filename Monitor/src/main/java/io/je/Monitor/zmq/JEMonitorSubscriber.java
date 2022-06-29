package io.je.Monitor.zmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.je.Monitor.config.MonitorProperties;
import io.siothconfig.SIOTHConfigUtility;

import java.util.Arrays;
import java.util.HashSet;

@Component
public class JEMonitorSubscriber {

	@Autowired
	MonitoringSubscriber subscriber;
	
    @Autowired
    MonitorProperties monitorProperties;

    public static final String JEMONITOR_TOPIC = "JEMonitorTopic";

    public void initSubscriber() {
        subscriber.setConfig("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
                monitorProperties.getMonitoringPort(), new HashSet<>(Arrays.asList(JEMONITOR_TOPIC)));
    	Thread thread = new Thread(subscriber);
        subscriber.setListening(true);
        thread.start();
    }

}
