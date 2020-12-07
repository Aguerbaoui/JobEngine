package builder;

import java.util.ArrayList;
import java.util.HashMap;

import blocks.WorkflowBlock;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.AddWorkflowBlockException;
import models.JEWorkflow;

public class WorkflowBuilder {

	public static HashMap<String, HashMap<String, JEWorkflow>> workflows; 
	
	public static void addWorkflowBlock(WorkflowBlock block) throws AddWorkflowBlockException {
		
		if(workflows.get(block.getProjectId()) == null) {
			throw new AddWorkflowBlockException("1", Errors.getMessage(1));
		}
	}
	
	public static void addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef, String condition) {
		
	}
}
