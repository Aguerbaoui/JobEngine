package io.je.utilities.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.zmq.ZMQPublisher;

public class ZMQLogPublisher {
	
	//TODO: read from config instead of hardcoded msg
	static ZMQPublisher publisher = new ZMQPublisher("tcp:\\localhost", 15001);
	static ObjectMapper objectMapper = new ObjectMapper();

	public static void publish(LogMessageFormat msg) {
		try {
			String jsonMsg = objectMapper.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		publisher.publish(msg.toString(), "SIOTH##LogTopic");
		
	}
	
	
	

}
