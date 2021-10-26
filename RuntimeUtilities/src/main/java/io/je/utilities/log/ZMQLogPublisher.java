package io.je.utilities.log;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.siothconfig.SIOTHConfigUtility;
import utils.log.*;
import utils.zmq.ZMQPublisher;

import java.util.Arrays;

public class ZMQLogPublisher {

	//TODO: read from config instead of hardcoded msg
	
	
	static ZMQPublisher publisher = new ZMQPublisher("tcp://"+SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode() , SIOTHConfigUtility.getSiothConfig().getPorts().getTrackingPort());
	static ObjectMapper objectMapper = new ObjectMapper();

	public static void publish(LogMessage msg) {
		try {
			String jsonMsg = objectMapper.writeValueAsString(msg);
			publisher.publish(jsonMsg, "SIOTH##LogTopic");
			//System.out.println(jsonMsg);

		} catch (Exception e) {
			// TODO : replace with custom exception
			JELogger.error("Failed to publish log message. " + Arrays.toString(e.getStackTrace()),
					LogCategory.RUNTIME, null,
					LogSubModule.JERUNNER, null);
		}
		
	}
	
	
	
	
	

}
