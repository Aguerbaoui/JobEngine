package io.je.runtime.data;

import io.je.runtime.beans.DMTopic;
import io.je.runtime.beans.DMListener;
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
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataModelListener {

    /*
     * Map of topic-listener
     * */
    private static Thread activeThread = null;
    private static ZMQAgent agent = null;
    private static ObjectMapper objectMapper = new ObjectMapper();
	static Map<String,DMTopic> allDMTopics = new HashMap<>();
	
	
	
	public static List<String> getTopicsByProjectId(String projectId){
		List<String> topics = new ArrayList<>();
		for(DMTopic topic : allDMTopics.values())
		{
			if(topic.getProjects().contains(projectId))
			{
				topics.add(topic.getId());
			}
		}
		return topics;
	}
	
	public static List<String> getRuleTopicsByProjectId(String projectId){
		List<String> topics = new ArrayList<>();
		for(DMTopic topic : allDMTopics.values())
		{
			if(topic.getProjects().contains(projectId) )
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

    public static void startListening(List<String> topics) {
        JELogger.debug(JEMessages.LISTENING_ON_TOPICS + topics,  LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);
    	for (String id : topics)
    	{
			readInitialValues(id);
		}

		if (agent!=null && !agent.isListening())
		{
			agent.setListening(true);
			if (activeThread != null) {
				activeThread.interrupt();
				activeThread = null;
			}
			activeThread = new Thread(agent);
			activeThread.start();
		}
    }

    private static void readInitialValues(String modelId) {
		List<Object> initialValues = DataModelRequester.readInitialValues(modelId);
		for(Object value : initialValues)
		{
     		 try {				
				RuntimeDispatcher.injectData(new JEData(modelId, objectMapper.writeValueAsString(value)));
				
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	public static void stopListening(List<String> topics) {
        JELogger.control(JEMessages.STOPPED_LISTENING_ON_TOPICS + topics,  LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);

            try {
				agent.removeTopics(topics);
            }
            catch (Exception e) {
                JELogger.error(JEMessages.INTERRUPT_TOPIC_ERROR + topics.toString(),  LogCategory.RUNTIME,
                        null, LogSubModule.JERUNNER, e.getMessage());
            }
    }

	public static void addDMListener(DMListener dMListener, Set<String> topics) {
		createNewZmqAgent(topics);

		for(String topic : topics)
		{
			if(!allDMTopics.containsKey(topic))
			{
				allDMTopics.put(topic, new DMTopic(topic)) ;
			}
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
			List<String> temp = new ArrayList<>();
			temp.add(topic.getId());
			stopListening(temp);
		}
	}

	public static List<String> getProjectsSubscribedToTopic(String topic) {
		
		return allDMTopics.get(topic).getProjects();
		
	}

}
