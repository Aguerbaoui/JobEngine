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

        final String ID_MSG = "Monitor Subscriber : ";

        try {

            this.addTopic(JEMONITOR_TOPIC, ZMQType.BIND);

            JELogger.debug(ID_MSG + "topics : " + this.topics + " : "
                            + JEMessages.STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE,
                    LogCategory.MONITOR, null, LogSubModule.JEMONITOR, null);

            String data, last_topic = null;

            while (this.listening) {

                data = this.getSubscriberSocket(ZMQType.BIND).recvStr();

                if (data == null) continue;

                LoggerUtils.debug(ID_MSG + JEMessages.DATA_RECEIVED + data);

                // FIXME waiting to have topic in the same response message
                if (last_topic == null) {

                    for (String topic : this.topics) {
                        // Received Data should be equal topic
                        if (data.equals(topic)) {
                            last_topic = topic;
                            break;
                        }
                    }

                } else {

                    JELogger.debug(ID_MSG + "WebSocketService : send updates : " + data);

                    this.service.sendUpdates(data);

                    last_topic = null;
                }

            }

        } catch (ZMQConnectionFailedException e) {

            LoggerUtils.logException(e);

            JELogger.error(ID_MSG + JEMessages.ZMQ_CONNECTION_FAILED, LogCategory.DESIGN_MODE, null,
                    LogSubModule.CLASS, e.getMessage());

        } catch (Exception e) {

            LoggerUtils.logException(e);

            JELogger.error(ID_MSG + e.getMessage(), LogCategory.MONITOR, "", LogSubModule.CLASS, "");

        } finally {

            JELogger.debug(ID_MSG + JEMessages.CLOSING_SOCKET,
                    LogCategory.MONITOR, null, LogSubModule.JEMONITOR, null);

            try {
                this.removeTopic(JEMONITOR_TOPIC, ZMQType.BIND);
            } catch (Exception e) {
                LoggerUtils.logException(e);
            }

            this.closeSocket();

        }

    }

}
