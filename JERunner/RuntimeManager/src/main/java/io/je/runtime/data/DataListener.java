package io.je.runtime.data;

import io.je.utilities.constants.JEGlobalconfig;
import io.je.utilities.logger.JELogger;

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
            ZMQAgent agent = new ZMQAgent(JEGlobalconfig.DATA_MANAGER_BASE_API, JEGlobalconfig.SUBSCRIBER_PORT, JEGlobalconfig.REQUEST_PORT, topic);
            agents.put(topic, agent);
        }
        else {
            agents.get(topic).incrementSubscriptionCount();
        }
    }

    public static void startListening(List<String> topics) {
    	for (String id : topics)
    	{
    		ZMQAgent agent = agents.get(id);
    		agent.setListening(true);
    		Thread thread = new Thread(agent);
    		activeThreads.put(id, thread);
    		thread.start();
    	}
    
    }

    public static void stopListening(List<String> topics) {
        for (String id : topics)
        {
            agents.get(id).setListening(false);
            try {
                activeThreads.remove(id);
            }
            catch (Exception e) {
                JELogger.error(DataListener.class, "Error interrupting thread for topic = " + id);
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
        agents.get(topic).incrementSubscriptionCount();
    }

/*
    public static void startListeningOnTopic(String topic) throws DataListenerNotFoundException {
        if(!activeTopics.containsKey(topic)) {
            throw new DataListenerNotFoundException(ResponseCodes.DATA_LISTENER_NOT_FOUND, Errors.DATA_LISTENER_NOT_FOUND);
        }
        activeTopics.get(topic).setListening(true);
    }

    public static void stopListeningOnTopic(String topic) throws DataListenerNotFoundException {
        if(!activeTopics.containsKey(topic)) {
            throw new DataListenerNotFoundException(ResponseCodes.DATA_LISTENER_NOT_FOUND, Errors.DATA_LISTENER_NOT_FOUND);
        }
        activeTopics.get(topic).setListening(false);
    }
    
    */
}
