package io.je.runtime.data;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEData;
import io.je.utilities.config.Utility;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.InstanceCreationFailed;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.zmq.ZMQRequester;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class DataListener {

    /*
     * Map of topic-listener
     * */
    private static HashMap<String , Thread> activeThreads = new HashMap<String, Thread>();
    private static HashMap<String, ZMQAgent> agents = new HashMap<String, ZMQAgent>();
    public static ObjectMapper objectMapper = new ObjectMapper();
    private static 	TypeFactory typeFactory = objectMapper.getTypeFactory();


    /*
     * ZMQ Request to DataModel to read last values for specific class(by ModelId)
     */
    private static void readInitialValues(String topic) {
    	JELogger.trace("Loading last values for topic = " +topic ,  LogCategory.RUNTIME,
				null, LogSubModule.JERUNNER, null);
    	try {
    		
    		
    		ZMQRequester requester = new ZMQRequester("tcp://"+Utility.getSiothConfig().getMachineCredentials().getIpAddress(), Utility.getSiothConfig().getDataModelPORTS().getDmService_ReqAddress());
    	 	HashMap<String,String> requestMap = new HashMap<String, String>();
        	requestMap.put("Type", "ReadInitialValues");
        	requestMap.put("ModelId", topic);
			String data = requester.sendRequest(objectMapper.writeValueAsString(requestMap));
			JELogger.trace(JEMessages.DATA_RECEIVED + data,  LogCategory.RUNTIME,
					null, LogSubModule.JERUNNER, null);
			 if( data !=null )
				{ 
					List<String> values = objectMapper.readValue(data, typeFactory.constructCollectionType(List.class, String.class));
					for(String value : values)
					{
		         		 try {
							RuntimeDispatcher.injectData(new JEData(topic, value));
						} catch (InstanceCreationFailed e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					
				}
		} catch (JsonProcessingException e) {
			JELogger.error("Failed to read last values for topic : " + topic , null, "", LogSubModule.JERUNNER, topic);
		}
    	
    }
    
    public static void subscribeToTopic(String topic )
    {
    	createNewZmqAgent(topic);
    }

    private static void createNewZmqAgent(String topic) {
        if(!agents.containsKey(topic)) {
            ZMQAgent agent = new ZMQAgent("tcp://"+Utility.getSiothConfig().getMachineCredentials().getIpAddress(), Utility.getSiothConfig().getDataModelPORTS().getDmService_PubAddress(), topic);
            agents.put(topic, agent);
        }
        else {
            agents.get(topic).incrementSubscriptionCount();
        }
    }

    public static void startListening(List<String> topics) {
        JELogger.debug(JEMessages.LISTENING_ON_TOPICS + topics,  LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);
    	for (String id : topics)
    	{
    		readInitialValues(id);
    		ZMQAgent agent = agents.get(id);
    		{
    			if (agent!=null && !agent.isListening())
    			{
    				agent.setListening(true);
    	    		Thread thread = new Thread(agent);
    	    		activeThreads.put(id, thread);
    	    		thread.start();
    			}
    		}
    	}
    
    }

    public static void stopListening(List<String> topics) {
        JELogger.debug(JEMessages.STOPPED_LISTENING_ON_TOPICS + topics,  LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);
        for (String id : topics)
        {
        	 if(agents.containsKey(id))
        	 {
        		 agents.get(id).setListening(false);
        	 }
            try {
            	
                activeThreads.remove(id);
            }
            catch (Exception e) {
                JELogger.error(JEMessages.INTERRUPT_TOPIC_ERROR + id,  LogCategory.RUNTIME,
                        null, LogSubModule.JERUNNER, null);
            }
        }
    }

    public static void decrementSubscriptionCount(String topic) {
        if(agents.containsKey(topic)) {
            agents.get(topic).decrementSubscriptionCount();
            if (agents.get(topic).getSubscribers() <= 0) {
                ZMQAgent agent = agents.remove(topic);
                agent = null;
            }
        }
    }

    public static void incrementSubscriptionCount(String topic) {
        if(agents.get(topic)==null)
        {
        	createNewZmqAgent(topic);
        }
    	agents.get(topic).incrementSubscriptionCount();

    }

	public static String getTopics() {
		return agents.keySet().toString();
		
	}


}
