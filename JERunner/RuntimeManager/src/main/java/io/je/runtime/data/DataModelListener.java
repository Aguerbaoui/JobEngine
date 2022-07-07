package io.je.runtime.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.runtime.beans.DMListener;
import io.je.runtime.beans.DMTopic;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEData;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.instances.DataModelRequester;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.util.*;
import java.util.Map.Entry;

public class DataModelListener {

    /*
     * Map of topic-listener
     * */
    private static Thread activeThread = null;
    private static ZMQAgent agent = null;
    private static ObjectMapper objectMapper = new ObjectMapper();
	static Map<String,DMTopic> allDMTopics = new HashMap<>();
	
	
	
	public static Set<String> getTopicsByProjectId(String projectId){
		Set<String> topics = new HashSet<>();
		for(DMTopic topic : allDMTopics.values())
		{
			if(topic.getProjects().contains(projectId))
			{
				topics.add(topic.getId());
			}
		}
		return topics;
	}
	
	public static Set<String> getRuleTopicsByProjectId(String projectId) {
		Set<String> topics = new HashSet<>();
		for(DMTopic topic : allDMTopics.values())
		{
			if (topic.getProjects().contains(projectId))
			{
				for(DMListener subscriber : topic.getListeners().values())
				{
					if(subscriber.getType().equals("rule"))
					{
						topics.add(topic.getId());
					}
				}
			}
		}
		return topics;
	}

    private static void createNewZmqAgent(Set<String> topics) {

		agent = new ZMQAgent("tcp://"+SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
						SIOTHConfigUtility.getSiothConfig().getDataModelPORTS().getDmService_PubAddress(), topics);

    }

    public static void startListening(Set<String> topics) {
        JELogger.debug(JEMessages.LISTENING_ON_TOPICS + topics,  LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);

		if (activeThread != null) {
			activeThread.interrupt();
			activeThread = null;
		}

		if (agent != null) {
			agent.stopListening();
			agent = null;
		}

		createNewZmqAgent(topics);

		activeThread = new Thread(agent);
		activeThread.start();

    }

	/*
        ZMQ is missing last value caching (LVC)
        https://zguide.zeromq.org/docs/chapter5/

        We do it by requestInitialValues
     */
    public static void requestInitialValues(String topic) {
		List<Object> initialValues = new ArrayList<>();

		String [] splittedTopic = topic.split("#");
		if (splittedTopic.length > 1) {
			// Case topic containing instance Id
			initialValues = Arrays.asList(DataModelRequester.getLastInstanceValue(splittedTopic[1], false));
		} else if (splittedTopic.length == 1) {
			// Case topic containing just modelId (Class)
			initialValues = DataModelRequester.readInitialValues(splittedTopic[0]);
		}

		for(Object value : initialValues)
		{
     		 try {				
				RuntimeDispatcher.injectData(new JEData(topic, objectMapper.writeValueAsString(value)));
				
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	public static void stopListening(Set<String> topics) {
        JELogger.trace(JEMessages.STOPPING_LISTENING_TO_TOPICS + topics,  LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);

		try {
			agent.stopListening();
		}
		catch (Exception e) {
			JELogger.error(JEMessages.INTERRUPT_TOPIC_ERROR + topics.toString(),  LogCategory.RUNTIME,
					null, LogSubModule.JERUNNER, e.getMessage());
		}
    }

	public static void resetDMListener(DMListener dMListener, Set<String> topics) {
		createNewZmqAgent(topics);

		allDMTopics = new HashMap<>();

		for(String topic : topics)
		{
			allDMTopics.put(topic, new DMTopic(topic)) ;
			allDMTopics.get(topic).addListener(dMListener);
		}
	}
	
	public static void removeDMListener(String listenerId) {
		for(Entry<String, DMTopic> topic : allDMTopics.entrySet())
		{
			if(topic.getValue().hasListener(listenerId))
			{
				topic.getValue().removeListener(listenerId);
			}
			stopListeningOnTopicIfNoSubscribers(topic.getValue());
					
		}
		
	}

	public static void removeListenersByProjectId(String projectId) {
		for(Entry<String, DMTopic> topic : allDMTopics.entrySet())
		{
			topic.getValue().removeAllProjectListeners(projectId);
			stopListeningOnTopicIfNoSubscribers(topic.getValue());
		
		}
		
	}
	
	public static void stopListeningOnTopicIfNoSubscribers(DMTopic topic)
	{
		if(!topic.hasListeners())
		{
			Set<String> temp = new HashSet<>();
			temp.add(topic.getId());
			stopListening(temp);
		}
	}

	public static List<String> getProjectsSubscribedToTopic(String topic) {
		
		return allDMTopics.get(topic).getProjects();
		
	}

}
