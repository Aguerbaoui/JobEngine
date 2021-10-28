package io.je.runtime.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DMTopic {

	String id;
	Map<String,DMListener> dMListeners;
	List<String> projects;
	
	
	
	
	public DMTopic(String id) {
		super();
		this.id = id;
		dMListeners = new ConcurrentHashMap<>();
		projects = new ArrayList<>();
	}

	
	public boolean hasListener(String listenerId)
	{
		return dMListeners.containsKey(listenerId);
	}
	
	public boolean hasListeners()
	{
		return !dMListeners.isEmpty();
	}

	public List<String> getRuleListenersByProjectId(String projectId)
	{
		List<String> result = new ArrayList<>();
		for(Entry<String, DMListener> dMListener: dMListeners.entrySet())
		{
			if(!result.contains(dMListener.getValue().getId()) && dMListener.getValue().getProjectId().equals(projectId)  &&  dMListener.getValue().getType().equals("rule"))
			{
				result.add(dMListener.getValue().getId());
			}
		}
		return result;
	}
	
	
	public List<String> getProjects()
	{		
		return projects.stream()
                .distinct()
                .collect(Collectors.toList());
	}
	
	
	public void addListener(DMListener dMListener)
	{
		if(!dMListeners.containsKey(dMListener.id)) {
			dMListeners.put(dMListener.id, dMListener);
			
			}
		projects.add(dMListener.projectId);

		
	}
	
	public void removeListener(String listenerId)
	{
		if(dMListeners.containsKey(listenerId)) {
			projects.remove(dMListeners.get(listenerId).projectId);
			dMListeners.remove(listenerId);

			}


	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


	public Map<String, DMListener> getListeners() {
		return dMListeners;
	}


	public void removeAllProjectListeners(String projectId) {
		for( DMListener dMListener: dMListeners.values())
		{
			if(dMListener.getProjectId().equals(projectId))
			{
				dMListeners.remove(dMListener.id);
			}
		}
		
	}



	

	
	
}
