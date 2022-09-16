package io.je.Monitor.zmq;

import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JEMonitorSubscriber {

    public static final String JEMONITOR_TOPIC = "JEMonitorTopic";

    @Autowired
    MonitorZMQSubscriber monitorZMQSubscriber;


    public void initSubscriber(int monitoringPort) {

        // TODO enhance code
        monitorZMQSubscriber.setUrl("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode());
        monitorZMQSubscriber.setSubscriberPort(monitoringPort);

        monitorZMQSubscriber.setConnectionAddress(monitorZMQSubscriber.getUrl() + ":" + monitorZMQSubscriber.getSubscriberPort());

        Thread thread = new Thread(monitorZMQSubscriber);
        thread.start();

    }

}
