package io.je.utilities.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogMessage;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQPublisher;

public class ZMQLogPublisher {

    //TODO: read from config instead of hardcoded msg

    static ZMQPublisher publisher = new ZMQPublisher("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(), SIOTHConfigUtility.getSiothConfig().getPorts().getTrackingPort());
    static ObjectMapper objectMapper = new ObjectMapper();

    public static void publish(LogMessage msg) {

        try {

            String jsonMsg = objectMapper.writeValueAsString(msg);

            publisher.publish(jsonMsg, "SIOTH##LogTopic");

            LoggerUtils.trace("Publish json log message : " + jsonMsg);

        } catch (Exception e) {
            JELogger.logException(e);
            // TODO : replace with custom exception
            JELogger.error("Failed to publish log message : " + e.getMessage(),
                    LogCategory.RUNTIME, null,
                    LogSubModule.JERUNNER, null);
        }

    }

    public static void close() {
        publisher.closeSocket();
    }

}
