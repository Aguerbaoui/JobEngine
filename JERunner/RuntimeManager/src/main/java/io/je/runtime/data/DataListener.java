package io.je.runtime.data;

import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.JEGlobalconfig;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.DataListenerNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DataListener {

    /*
     * Map of modelId-InstanceId
     * */
    private static HashMap<String , Thread> activeThreads = null;
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
    }

    public static void startListening(List<String> topics) {
        //activeThreads = new HashMap<>();
    	for (String id : topics)
    	{
    		ZMQAgent agent = agents.get(id);
    		Thread thread = new Thread(agent);
    		activeThreads.put(id, thread);
    		thread.start();
    	}
    
    }

    public static void stopListening(List<String> topics) {
        for (String id : topics)
        {
            agents.get(id).setListening(false);
        }
        //activeThreads = null;
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
