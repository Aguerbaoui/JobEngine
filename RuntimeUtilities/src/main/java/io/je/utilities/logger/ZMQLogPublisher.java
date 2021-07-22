package io.je.utilities.logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.config.Utility;
import io.je.utilities.zmq.ZMQPublisher;

public class ZMQLogPublisher {
	
	//TODO: read from config instead of hardcoded msg
	
	
	static ZMQPublisher publisher = new ZMQPublisher("tcp://"+Utility.getSiothConfig().getMachineCredentials().getIpAddress() , Utility.getSiothConfig().getPorts().getLogServicePort());
	static ObjectMapper objectMapper = new ObjectMapper();

	public static void publish(LogMessage msg) {
		try {
			String jsonMsg = objectMapper.writeValueAsString(msg);
			publisher.publish(jsonMsg, "SIOTH##LogTopic");
			System.out.println(jsonMsg);

		} catch (Exception e) {
			// TODO : replace with custom exception
			e.printStackTrace();
			JELogger.error("Failed to send log message to the logging system : " +e.getMessage());
		}
		
	}
	
	
	
	
	

}
