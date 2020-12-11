package io.je.utilities.constants;

public class Errors {

	
	public final static String workflowNotFound = "Workflow not found";
	
	public final static String projectNotFound = "Project not found";
	
	public final static String uknownError = "Uknown error";

	public static String getMessage(int i) {
		
		switch(i) {
		
		case 1: return workflowNotFound;
		
		case 2: return projectNotFound;
		
		default: return "Uknown Error";
		}
		
	}
}
