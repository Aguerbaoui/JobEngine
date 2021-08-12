package io.je.runtime.data;

import io.je.utilities.config.Utility;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;

import java.util.HashMap;
import java.util.List;

public class DataListener {

    /*
     * Map of topic-listener
     * */
    private static HashMap<String , Thread> activeThreads = new HashMap<String, Thread>();
    private static HashMap<String, ZMQAgent> agents = new HashMap<String, ZMQAgent>();


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
