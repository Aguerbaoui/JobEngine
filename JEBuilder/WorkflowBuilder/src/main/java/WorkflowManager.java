import java.util.ArrayList;

import models.JEWorkflow;

/*
 * Workflow Manager class
 * */
public class WorkflowManager {

	public ArrayList<JEWorkflow> workflowList;
	
	public void addWorkflow(JEWorkflow wf) {
		if(workflowList == null) {
			workflowList = new ArrayList<JEWorkflow>();
		}
		
		workflowList.add(wf);
	}
}
