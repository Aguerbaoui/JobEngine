package io.je.Monitor.zmq;

import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.log.LoggerUtils;

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

        interruptThread();

        initThread();

    }

    private void initThread() {

        monitorZMQSubscriberThread = new Thread(monitorZMQSubscriber);

        monitorZMQSubscriberThread.setName("monitorZMQSubscriberThread");

        monitorZMQSubscriberThread.start();

    }

    private void interruptThread() {

        if (monitorZMQSubscriberThread != null) {
            if (monitorZMQSubscriberThread.isAlive()) {
                monitorZMQSubscriberThread.interrupt();
            }
            monitorZMQSubscriberThread = null;
        }
    }

    public void close() {

        // Interrupt Thread before closing socket to avoid org.zeromq.ZMQException: Errno 4
        interruptThread();

        if (monitorZMQSubscriber != null) {
            LoggerUtils.trace("Setting monitorZMQSubscriber listening to false.");
            monitorZMQSubscriber.setListening(false);
            monitorZMQSubscriber.closeSocket();
            monitorZMQSubscriber = null;
        }

    }

}
