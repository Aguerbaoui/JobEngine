package io.je.utilities.constants;

public class Errors {

	
	public final static String workflowNotFound = "Workflow not found";

	public static String getMessage(int i) {
		
		switch(i) {
		
		case 1: return workflowNotFound;
		
		default: return "Uknown Error";
		}
		
	}
}
