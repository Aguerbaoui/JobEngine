package io.je.ruleengine.interfaces;

import io.je.ruleengine.impl.ProjectContainer;

public interface ProjectContainerRepositoryInterface {
	
		private ProjectContainer newProjectContainer(String projectId) {
			return null;
		}
		public ProjectContainer getProjectContainer(String projectId);
		public void deleteProjectContainer(String projectId);
		
		
		

}
