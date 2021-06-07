package io.je.utilities.constants;

public class APIConstants {



   // public  static String RUNTIME_MANAGER_BASE_API = "http://127.0.0.1:8182/RuntimeManager-0.0.1.war";

    public final static String ADD_TOPIC= "/project/addTopics";

    public final static String ADD_WORKFLOW = "/workflow/addWorkflow";

    public final static String RUN_WORKFLOW = "/workflow/runWorkflow/";

    public final static String ADD_VARIABLE = "/variable/addVariable/";

    public final static String ADD_JAR = "/uploadJar";

    public final static String DELETE_VARIABLE = "/variable/deleteVariable/";


    
    public final static String COMPILERULE= "/rule/compileRule";

    


    public static final int SUBSCRIBER_TIMEOUT = 1000;

    public static final int REQUESTER_TIMEOUT = 5000;

	public static final String ADD_RULE =  "/rule/addRule";

	public static final String UPDATERULE = "/rule/updateRule";

	public static final String ADD_CLASS = "/addClass";

	public static final String ADD_CLASSES = "/addClasses";

	public static final String TRIGGER_EVENT = "/event/triggerEvent/";

	public static final String ADD_EVENT = "/event/addEvent";

	public static final String UPDATE_EVENT = "/event/updateEventType";

	public static final String DELETE_EVENT = "/event/deleteEvent";

	public static final String DELETE_WORKFLOW = "/workflow/deleteWorkflow";

	public static final String RUN_PROJECT = "/project/runProject/";

	public static final String CLEAN_HOUSE = "/project/removeProjectData/";

	public static final String STOP_PROJECT = "/project/stopProject/";

    public static final String DEFAULT =  "DEFAULT";

	public static final String DELETERULE = "/rule/deleteRule";

	public static final String ACTUATOR_HEALTH = "/actuator/health";

	public static final String PROJECT_UPDATE_RUNNER = "/config/updateRunner";

	public static final String EVENT_ADD_EVENT = "/event/addEvent";

	public static final String EVENT_TRIGGER_EVENT = "/event/triggerEvent/";
	
	public static final String EVENT_UNTRIGGER_EVENT = "/event/untriggerEvent/";
	
	public static final String WRITE_TO_VARIABLE = "/variable/writeVariableValue/";
	


	public static final String UPDATE_CONFIG = "/updateConfig";

}
