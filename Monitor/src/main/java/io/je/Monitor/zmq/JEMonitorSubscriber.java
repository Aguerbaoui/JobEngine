package io.je.Monitor.zmq;

import io.je.Monitor.config.MonitorProperties;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JEMonitorSubscriber {

	@Autowired
    MonitorZMQSubscriber monitorZMQSubscriber;
	
    @Autowired
    MonitorProperties monitorProperties;

    public static final String JEMONITOR_TOPIC = "JEMonitorTopic";

    public void initSubscriber() {
        monitorZMQSubscriber.setConfig("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
                monitorProperties.getMonitoringPort());

    	Thread thread = new Thread(monitorZMQSubscriber);
        thread.start();
    }

}
