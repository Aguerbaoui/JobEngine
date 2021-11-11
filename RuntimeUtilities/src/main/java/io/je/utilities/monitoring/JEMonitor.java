package io.je.utilities.monitoring;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.beans.ArchiveOption;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import org.zeromq.ZMQ;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQPublisher;

public class JEMonitor  {

	static ObjectMapper objectMapper = new ObjectMapper();
	static ZMQ.Context  mContext  = ZMQ.context(1);
	static ZMQ.Socket publisher = mContext.socket(ZMQ.PUB);
	static boolean init = false;
	public static void publish(MonitoringMessage msg) {
		if(!init) {
			publisher.bind("tcp://*:15020");
			init = true;
		}

		try {
			String jsonMsg = objectMapper.writeValueAsString(msg);
			String update = "JEMonitorTopic:" + jsonMsg;
			publisher.send(update, 0);

		} catch (Exception e) {
			JELogger.error(JEMessages.FAILED_TO_SEND_MONITORING_MESSAGE_TO_THE_LOGGING_SYSTEM,
					LogCategory.RUNTIME, null,
					LogSubModule.JERUNNER, null);
		}
		
	}
	

}
