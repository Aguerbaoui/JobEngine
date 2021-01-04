package io.je.utilities.constants;

public class Errors {


    public final static String workflowNotFound = "Workflow not found";

    public final static String workflowBlockNotFound = "Workflow block not found";

    public final static String projectNotFound = "Project not found";

    public final static String uknownError = "Uknown error";

    public final static String InvalidSequenceFlow = "Invalid Sequence flow";

    public final static String NETWORK_ERROR = "Error connecting to runtime manager api";
    
    
    
    
    
    

    /*public static String getMessage(String i) {

        int code = Integer.parseInt(i);
        switch (code) {

            case APIConstants.WORKFLOW_NOT_FOUND:
                return workflowNotFound;

            case APIConstants.PROJECT_NOT_FOUND:
                return projectNotFound;

            case APIConstants.WORKFLOW_BLOCK_NOT_FOUND:
                return workflowBlockNotFound;

            case APIConstants.INVALID_SEQUENCE_FLOW:
                return InvalidSequenceFlow;

            default:
                return "Unknown Error";
        }

    }*/
}
