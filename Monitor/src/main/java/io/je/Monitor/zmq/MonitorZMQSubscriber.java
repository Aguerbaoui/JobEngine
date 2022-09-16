package io.je.Monitor.zmq;

import io.je.Monitor.service.WebSocketService;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQConnectionFailedException;
import utils.zmq.ZMQSubscriber;
import utils.zmq.ZMQType;

import static io.je.Monitor.zmq.JEMonitorSubscriber.JEMONITOR_TOPIC;


//@AutoConfigureAfter(value = { MonitorProperties.class, JEMonitorInitializingBean.class })
@Component
public class MonitorZMQSubscriber extends ZMQSubscriber {

    @Autowired
    WebSocketService service;


    public MonitorZMQSubscriber() {
        super();
    }


    @Override
    public void run() {

        synchronized (this) {

            final String ID_MSG = "Monitor Subscriber : ";

            JELogger.debug(ID_MSG + JEMessages.STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE,
                    LogCategory.MONITOR, null, LogSubModule.JEMONITOR, null);

            String last_topic = null;

            while (this.listening) {
                String data = null;

                try {

                    this.addTopic(JEMONITOR_TOPIC, ZMQType.BIND);

                } catch (ZMQConnectionFailedException e) {
                    LoggerUtils.logException(e);
                    JELogger.error(ID_MSG + JEMessages.ZMQ_CONNECTION_FAILED, LogCategory.DESIGN_MODE, null,
                            LogSubModule.CLASS, e.getMessage());
                }

                try {

                    data = this.getSubscriberSocket(ZMQType.BIND).recvStr();

                    if (data == null) continue;

                    // FIXME waiting to have topic in the same response message
                    if (last_topic == null) {
                        for (String topic : this.topics) {
                            // Received Data should be equal topic
                            if (data.equals(topic)) {
                                last_topic = topic;

                                JELogger.debug(ID_MSG + "data received : topic : " + topic);
                                break;
                            }
                        }
                    } else {

                        JELogger.debug(ID_MSG + "WebSocketService : send updates : " + data);

                        this.service.sendUpdates(data);

                        last_topic = null;
                    }
                } catch (Exception e) {
                    JELogger.error(e.toString(), null, "", null, "");
                }

            }

            JELogger.debug(ID_MSG + JEMessages.CLOSING_SOCKET,
                    LogCategory.MONITOR, null, LogSubModule.JEMONITOR, null);

            this.closeSocket();

        }

    }

}
