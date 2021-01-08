package io.je.runtime.data;

import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.DataListenerNotFoundException;

import java.util.HashMap;
import java.util.Set;

public class DataListener {

    /*
     * Map of modelId-InstanceId
     * */
    private static HashMap<String, ZMQAgent> activeTopics;

    public static void addTopics(Set<String> topics) {
        while(topics.iterator().hasNext()) {
            String topic = topics.iterator().next();
            if(!activeTopics.containsKey(topic)) {
                activeTopics.put(topic, createNewZmqAgent(topic));
            }
            activeTopics.get(topic).prepareListener();
        }
    }

    private static ZMQAgent createNewZmqAgent(String topic) {
        return new ZMQAgent(APIConstants.DATA_MANAGER_BASE_API, APIConstants.SUBSCRIBER_PORT, APIConstants.REQUEST_PORT, topic);
    }

    public static void startListening() {
        for(ZMQAgent agent: activeTopics.values()) {
            agent.setListening(true);
        }
    }

    public static void stopListening() {
        for(ZMQAgent agent: activeTopics.values()) {
            agent.setListening(false);
        }
    }

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
}
