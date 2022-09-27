package io.je.Monitor.zmq;

import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JEMonitorSubscriber {

    public static final String JEMONITOR_TOPIC = "JEMonitorTopic";

    @Autowired
    MonitorZMQSubscriber monitorZMQSubscriber;

    Thread monitorZMQSubscriberThread = null;


    public void init(int monitoringPort) {

        // TODO enhance code
        monitorZMQSubscriber.setUrl("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode());
        monitorZMQSubscriber.setSubscriberPort(monitoringPort);

        monitorZMQSubscriber.setConnectionAddress(monitorZMQSubscriber.getUrl() + ":" + monitorZMQSubscriber.getSubscriberPort());

        monitorZMQSubscriberThread = new Thread(monitorZMQSubscriber);
        monitorZMQSubscriberThread.start();

    }

    public void close() {

        // Interrupt Thread before closing socket to avoid org.zeromq.ZMQException: Errno 4
        if (monitorZMQSubscriberThread != null) {
            if (monitorZMQSubscriberThread.isAlive()) {
                monitorZMQSubscriberThread.interrupt();
            }
        }

        if (monitorZMQSubscriber != null) {
            monitorZMQSubscriber.setListening(false);
            monitorZMQSubscriber.closeSocket();
        }

    }

}
