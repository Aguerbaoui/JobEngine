package io.je.utilities.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQPublisher;

public class JEMonitor {


    static int port;
    static ZMQPublisher publisher;
    static ObjectMapper objectMapper = new ObjectMapper();

    public static void publish(MonitoringMessage msg) {
        try {
            if (publisher == null) {
                publisher = new ZMQPublisher("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(), port);
            }

            String jsonMsg = objectMapper.writeValueAsString(msg);
            publisher.publish(jsonMsg, "JEMonitorTopic");
            //System.out.println(jsonMsg);

        } catch (Exception e) {
            JELogger.logException(e);
            // TODO : replace with custom exception
            JELogger.error("Failed to publish monitoring value. " + e.getMessage(),
                    LogCategory.RUNTIME, null,
                    LogSubModule.JERUNNER, null);
        }

    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        JEMonitor.port = port;
    }


}
