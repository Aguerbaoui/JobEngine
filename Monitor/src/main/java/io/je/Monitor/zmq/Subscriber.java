package io.je.Monitor.zmq;

import io.je.Monitor.service.WebSocketService;
import io.je.utilities.log.JELogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQSubscriber;

import static io.je.utilities.constants.JEMessages.STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE;

@Component
public class Subscriber implements Runnable {


    @Autowired
    WebSocketService service;

    private ZMQ.Context context;
    private ZMQ.Socket subscriber;

    private boolean listening;
    public Subscriber() {
        this.listening = false;
        context = ZMQ.context(1);
        subscriber = context.socket(ZMQ.SUB);
    }

    @Override
    public void run() {
        JELogger.control(STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE, LogCategory.MONITOR, null,
                LogSubModule.JEMONITOR, null);
        subscriber.connect("tcp://localhost:15020");
        subscriber.subscribe("JEMonitorTopic");
        while (listening) {
            String data = null;
            try {
                data = subscriber.recvStr();
                if(data != null) {
                    String finalData = data.replace("JEMonitorTopic:", "");
                    service.sendUpdates(finalData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }
}
