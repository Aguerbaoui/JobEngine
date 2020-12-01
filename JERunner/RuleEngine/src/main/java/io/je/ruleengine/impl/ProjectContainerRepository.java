package io.je.ruleengine.impl;

import java.util.HashMap;
import java.util.Map;
import io.je.ruleengine.interfaces.ProjectContainerRepositoryInterface;



public class ProjectContainerRepository implements ProjectContainerRepositoryInterface {
	
	//Map of all the projectContainers of this RuleEngine. 
	static Map<String,ProjectContainer> allProjects = new HashMap<>();

	private ProjectContainer newProjectContainer(String projectId) {
		ProjectContainer project = new ProjectContainer(projectId);
		allProjects.put(projectId, project);
		return project;
	}

	public ProjectContainer getProjectContainer(String projectId) {
		if(projectContainerExists(projectId))
		{
			return allProjects.get(projectId);
		}
		return  newProjectContainer(projectId);
	}

	public void deleteProjectContainer(String projectId) {
		if(projectContainerExists(projectId))
		{
			ProjectContainer project = allProjects.get(projectId);
			if(project.getStatus()==Status.RUNNING)
			{
				//TODO: error management
				project.stopRuleExecution();
			}
			project=null;
			allProjects.remove(projectId);
		}
		//else project doesn't exist
		

	}
	
	public boolean projectContainerExists(String projectId) {
		
		return allProjects.containsKey(projectId);
	}




}
