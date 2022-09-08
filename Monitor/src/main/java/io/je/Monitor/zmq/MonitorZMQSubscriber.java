package io.je.Monitor.zmq;

import io.je.Monitor.service.WebSocketService;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQBind;
import utils.zmq.ZMQSubscriber;

import static io.je.Monitor.zmq.JEMonitorSubscriber.JEMONITOR_TOPIC;
import static io.je.utilities.constants.JEMessages.STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE;

@Component
public class MonitorZMQSubscriber extends ZMQSubscriber {

    @Autowired
    WebSocketService service;

    public void setConfig(String url, int subPort) {
        this.url = url;
        this.port = subPort;
    }

    @Override
    public ZMQ.Socket getSubSocket() {
        boolean connectionSucceeded = false;

        if (socket == null) {

            JELogger.debug("MonitorZMQSubscriber : topic added : " + JEMONITOR_TOPIC,
                    LogCategory.MONITOR, null, LogSubModule.JEMONITOR, null);

            try {
                JELogger.info("Attempting to connect to address : " + url + ":" + port + "...", null, "", null, "");

                this.getSubSocket(ZMQBind.BIND);

                this.addTopic(JEMONITOR_TOPIC);

                // FIXME externalize messages
                JELogger.info("Connection succeeded", null, "", null, "");

                connectionSucceeded = true;

            } catch (Exception e) {
                connectionSucceeded = false;

                JELogger.error(e.getMessage(), null, "", null, "");

                this.closeSocket();
            }

            if (!connectionSucceeded) {
                JELogger.info(" Trying to establish connection with address: " + url + ":" + port + "...", null, "",
                        null, "");
            }

            while (!connectionSucceeded) {
                try {

                    this.getSubSocket(ZMQBind.BIND);

                    this.addTopic(JEMONITOR_TOPIC);

                    connectionSucceeded = true;
                    JELogger.info("Connection succeeded", null, "", null, "");

                } catch (Exception e) {
                    connectionSucceeded = false;

                    LoggerUtils.logException(e);

                    this.closeSocket();

                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LoggerUtils.logException(e);
                }

            }

            JELogger.control(STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE, LogCategory.MONITOR, null,
                    LogSubModule.JEMONITOR, null);
        }

        return socket;
    }

    @Override
    public void run() {

        synchronized (this) {

            final String ID_MSG = "Monitor Subscriber : ";

            JELogger.debug(ID_MSG + JEMessages.STARTED_LISTENING_FOR_DATA,
                    LogCategory.MONITOR, null, LogSubModule.JEMONITOR, null);

            String last_topic = null;

            while (this.listening) {
                String data = null;

                try {
                    data = this.getSubSocket().recvStr();

                    if (data == null) continue;

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

                        JELogger.debug(data);
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
