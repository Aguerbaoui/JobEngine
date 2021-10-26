package io.je.utilities.monitoring;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.beans.ArchiveOption;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.zmq.ZMQPublisher;

public class JEMonitor  {
	
	static ZMQPublisher publisher = new ZMQPublisher("tcp://"+SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode() , SIOTHConfigUtility.getSiothConfig().getPorts().getTrackingPort());
	static ObjectMapper objectMapper = new ObjectMapper();

	public static void publish(LocalDateTime timestamp, String objectId, ObjectType objectType, String objectProjectId,
			Object objectValue, ArchiveOption isArchived, boolean isBroadcasted) {
		try {
			MonitoringMessage msg = new MonitoringMessage(timestamp, objectId, objectType, objectProjectId, objectValue, isArchived, isBroadcasted);
			String jsonMsg = objectMapper.writeValueAsString(msg);
			publisher.publish(jsonMsg, "MonitoringTopic");
			//System.out.println(jsonMsg);

		} catch (Exception e) {
			// TODO : replace with custom exception
			JELogger.error(JEMessages.FAILED_TO_SEND_LOG_MESSAGE_TO_THE_LOGGING_SYSTEM + Arrays.toString(e.getStackTrace()),
					LogCategory.RUNTIME, null,
					LogSubModule.JERUNNER, null);
		}
		
	}
	

}
