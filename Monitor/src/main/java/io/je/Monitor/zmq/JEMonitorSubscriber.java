package io.je.Monitor.zmq;

import io.je.Monitor.config.MonitorProperties;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JEMonitorSubscriber {

    public static final String JEMONITOR_TOPIC = "JEMonitorTopic";
    @Autowired
    MonitorZMQSubscriber monitorZMQSubscriber;
    @Autowired
    MonitorProperties monitorProperties;

    public void initSubscriber() {
        monitorZMQSubscriber.setConfig("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
                monitorProperties.getMonitoringPort());

        Thread thread = new Thread(monitorZMQSubscriber);
        thread.start();
    }

}
