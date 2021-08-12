package io.je.utilities.logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.config.Utility;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.zmq.ZMQPublisher;

import java.util.Arrays;

public class ZMQLogPublisher {

	//TODO: read from config instead of hardcoded msg
	
	
	static ZMQPublisher publisher = new ZMQPublisher("tcp://"+Utility.getSiothConfig().getMachineCredentials().getIpAddress() , Utility.getSiothConfig().getPorts().getLogServicePort());
	static ObjectMapper objectMapper = new ObjectMapper();

	public static void publish(LogMessage msg) {
		try {
			String jsonMsg = objectMapper.writeValueAsString(msg);
			publisher.publish(jsonMsg, "SIOTH##LogTopic");
			//System.out.println(jsonMsg);

		} catch (Exception e) {
			// TODO : replace with custom exception
			JELogger.error(JEMessages.FAILED_TO_SEND_LOG_MESSAGE_TO_THE_LOGGING_SYSTEM + Arrays.toString(e.getStackTrace()),
					LogCategory.RUNTIME, null,
					LogSubModule.JERUNNER, null);
		}
		
	}
	
	
	
	
	

}
