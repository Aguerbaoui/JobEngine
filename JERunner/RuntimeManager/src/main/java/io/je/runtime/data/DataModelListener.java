package io.je.runtime.data;

import io.je.runtime.beans.DMTopic;
import io.je.runtime.beans.DMListener;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEData;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.InstanceCreationFailed;
import io.je.utilities.instances.DataModelRequester;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataModelListener {

    /*
     * Map of topic-listener
     * */
    private static HashMap<String , Thread> activeThreads = new HashMap<>();
    private static HashMap<String, ZMQAgent> agents = new HashMap<>();
    private static ObjectMapper objectMapper = new ObjectMapper();
	static Map<String,DMTopic> allTopics = new HashMap<>();
	
	
	
	public static List<String> getTopicsByProjectId(String projectId){
		List<String> topics = new ArrayList<>();
		for(DMTopic topic : allTopics.values())
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
		for(DMTopic topic : allTopics.values())
		{
			if(topic.getProjects().contains(projectId) )
			{
				topics.add(topic.getId());
			}
		}
		return topics;
	}
	

    
   /* public static void subscribeToTopic(String topic )
    {
    	createNewZmqAgent(topic);
    }
*/
    private static void createNewZmqAgent(String topic) {
       
            if(!agents.containsKey(topic))
            {
            	ZMQAgent agent = new ZMQAgent("tcp://"+SIOTHConfigUtility.getSiothConfig().getMachineCredentials().getIpAddress(), SIOTHConfigUtility.getSiothConfig().getDataModelPORTS().getDmService_PubAddress(), topic);
                agents.put(topic, agent);
            }
        

    }

    public static void startListening(List<String> topics) {
        JELogger.debug(JEMessages.LISTENING_ON_TOPICS + topics,  LogCategory.RUNTIME,
                null, LogSubModule.JERUNNER, null);
    	for (String id : topics)
    	{
    		if(!activeThreads.containsKey(id))
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
    
    }

    private static void readInitialValues(String modelId) {
		List<Object> initialValues = DataModelRequester.readInitialValues(modelId);
		for(Object value : initialValues)
		{
     		 try {				
				RuntimeDispatcher.injectData(new JEData(modelId, objectMapper.writeValueAsString(value)));
				
			} catch (InstanceCreationFailed | JsonProcessingException e) {
				e.printStackTrace();
			
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

	public static String getTopics() {
		return agents.keySet().toString();
		
	}

	public static void addDMListener(DMListener dMListener, List<String> topics) {
		for(String topic : topics)
		{
			if(!allTopics.containsKey(topic))
			{
				allTopics.put(topic, new DMTopic(topic)) ;
				createNewZmqAgent(topic);
			}
			allTopics.get(topic).addListener(dMListener);
			
		}
		
	}
	
	public static void removeDMListener(String listenerId) {
		for(Entry<String, DMTopic> topic : allTopics.entrySet())
		{
			if(topic.getValue().hasListener(listenerId))
			{
				topic.getValue().removeListener(listenerId);
			}
			stopListeningOnTopicIfNoSubscribers(topic.getValue());
					
		}
		
	}

	public static void removeListenersByProjectId(String projectId) {
		for(Entry<String, DMTopic> topic : allTopics.entrySet())
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
		
		return allTopics.get(topic).getProjects();
		
	}

}
