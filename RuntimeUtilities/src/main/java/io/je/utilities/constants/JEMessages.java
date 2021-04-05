package io.je.utilities.constants;

public class JEMessages {
	
	
	//*****************************************GLobalMessages******************************//
	public static final String BUILDER_STARTED = "";
	public static final String RUNNER_STARTED = "Builder started successfully";
	public static final String SETTING_ASYNC_EXECUTOR = "Setting async executor parameters";
	public static final String LOGGER_INITIALIZED = "Logger Initialized";
	public static final String INITILIZING_BUILDER = "Initializing builder";
	
	

	//*****************************************Configuration******************************//

	public static final String DATA_DEFINITION_URL_MISSING = "Data definition URL is missing";
	public static final String DATA_MODEL_URL_MISSING = "Data Manager URL is missing";
	public static final String JERUNNER_URL_MISSING = "JERunner URL is missing";
	public static final String DATA_MODEL_SUB_PORT_MISSING = "Subscriber port is missing";
	public static final String DATA_MODEL_REQ_PORT_MISSING = "Request port is missing";
	public static final String DROOLS_DATE_FORMAT_MISSING = "Drools date format is not specified";

	// ****************************************ClassBuilder**************************************************

	public static final String CLASS_NAME_NULL = "Class name cannot be empty";
	public static final String CLASS_UNKNOWN = "Class Unknown";
	public static final String TYPE_UNKNOWN = "Type Unknown";
	public static final String BUILDING_CLASS = "Building class..";
	public static final String CLASS_BUILD_FAILED = "Failed to build class ";
	public static final String CLASS_LOAD_FAILED = "Class load failed ";
	public static final String INVALID_CLASS_FORMAT = "Class format is not valid. ";
	public static final String UNKNOW_CLASS_TYPE = "Class type cannot be determined. [Class types : Class/Interface/Enum ] ";
	public static final String INHERITED_CLASS_ENUM = "Class cannot inherit from an enumeration";
	public static final String MULTIPLE_INHERITANCE = "Multiple inhertiance is not supported";
	public static final String CLASS_NOT_FOUND = "No Class with this id was found in DataModelRestApi";
	public static final String LOADING_ALL_CLASSES_FROM_DB = "Loading all classes from db to memory";
	
	// ****************************************TraceMessages**************************************************


	//PROJECT
	public static final String CREATING_PROJECT = "Creating project";
	public static final String SAVING_PROJECT = "Saving project";
	public static final String DELETING_PROJECT = "Deleting project ";
	public static final String BUILDING_PROJECT = "Building project";
	public static final String RUNNING_PROJECT = "Running project";
	public static final String STOPPING_PROJECT = "Stopping project";
	public static final String PROJECT_FOUND = "Found project";
	public static final String CLOSING_PROJECT = "Found project";
	public static final String LOADING_PROJECT = "Loading project from database";
	public static final String LOADING_PROJECTS = "loading all projects from database ";
	public static final String RESETTING_PROJECTS = " Resetting projects after updating runner";
	
	//WF
	public static final String LOADING_WFS = " Getting all workflows";
	public static final String LOADING_WF = "Getting workflow";
	public static final String ADDING_WF = "Adding workflow";
	public static final String REMOVING_WF = "Removing workflow";
	public static final String REMOVING_WFS = "Removing workflows";
	public static final String ADDING_WF_BLOCK = "Adding a workflow block";
	public static final String DELETING_WF_BLOCK = "Deleting  workflow block";
	public static final String ADDING_SEQUENCE_FLOW = "Adding a sequence flow with from";
	public static final String DELETING_SEQUENCE_FLOW = "Deleting a sequence flow with from";
	public static final String BUILDING_WF = "Building workflow";
	public static final String BUILDING_WFS = "Building workflows";
	public static final String RUNNING_WF = "Running workflow";
	public static final String RUNNING_WFS = "Running workflows";
	public static final String UPDATING_WF = "Updating workflow";
	public static final String ADDING_BPMN_SCRIPT = " Adding a bpmn script";


	//Rules
	public static final String ADDING_SCRIPTED_RULE = "Adding new scripted rule";
	public static final String UPDATING_SCRIPTED_RULE = "Updating new scripted rule";
	public static final String LOADING_RULES = " Getting all rules";
	public static final String ADDING_RULE = "Adding rule";
	public static final String UPDATING_RULE = "Updating rule";
	public static final String COMPILING_RULE = "Compiling rule";
	public static final String COMPILING_RULES = "Compiling all rules";
	public static final String LOADING_RULE = "Getting workflow";
	public static final String ADDING_BLOCK = "Adding block ";
	public static final String UPDATING_BLOCK = "Updating block ";
	public static final String DELETING_RULE = "Deleting rule";
	public static final String DELETING_RULES = "Deleting rules";
	public static final String DELETING_RULE_RUNNER = "Deleting rule from runner";
	public static final String DELETING_BLOCK = "Deleting block ";
	public static final String BUILDING_RULES = "Building all rules";
	public static final String BUILDING_RULE = "Building rule";
	public static final String SENDNG_RULE_TO_RUNNER = "Sending rule build request to runner";
	public static final String UPDATING_FACT = "Updating fact";


	// ****************************************ResponseMessages**************************************************


	public static final String DELETE_WORKFLOW_FAILED = "Error deleting  workflow";
	
	public static final String RUN_WORKFLOW_FAILED = "Error running workflow in runner : ";

	
	public static final String WORKFLOW_NOT_FOUND = "Workflow not found";

	public static final String WORKFLOW_BLOCK_NOT_FOUND = "Workflow block not found";

	public static final String PROJECT_NOT_FOUND = "Project not found";

	public static final String UKNOWN_ERROR = "Uknown error";

	public static final String INVALID_SEQUENCE_FLOW = "Invalid Sequence flow";

	public static final String NETWORK_ERROR = "Error connecting to runtime manager api";

	public static final String DATA_LISTENER_NOT_FOUND = "Data listener not found";

	public static final String DATA_DEFINITION_API_UNREACHABLE = "Data Definition Model Unreachable";

	public static final String DATA_DEFINITION_API_ERROR = "Data Definition Model Error";

	public static final String JERUNNER_UNREACHABLE = "JERunner Unreachable";

	public static final String JERUNNER_ERROR = "JERunner ERROR";

	public static final String DATA_DEFINITION_CLASS_NOT_FOUND = "No class with this Id was found";

	public static final String PROJECT_RUNNING = "PROJECT ALREADY RUNNING";

	public static final String PROJECT_BUILT = "PROJECT ALREADY BUILT";

	public static final String PROJECT_NOT_BUILT = "PROJECT NEEDS BUILD";

	public static final String PROJECT_ALREADY_STOPPED = "PROJECT ALREADY STOPPED";

	public static final String WORKFLOW_ALREADY_RUNNING = "Workflow ALREADY RUNNING";

	public static final String WORKFLOW_NEEDS_BUILD = "Workflow NEEDS BUILD";

	public static final String EVENT_NOT_FOUND = " No event with this id was found ";

	public static final String NOT_ALPHABETICAL = " Should only contain alphabetical characters ";

	public static final String WORKFLOW_TRIGGERED_BY_EVENT = " Workflow cant start manually";

	public static final String EVENT_ALREADY_EXISTS = "An event with this id already exists";

	public static final String MISSING_CONFIG = "Application configuration is missing. Make sure to properly configure the application before proceeding.";

	public static final String JEBUILDER_UNREACHABLE = " JEBuilder unreachable";

	// response messages
	public static final String PROJECT_EXISTS = "project exists";
	
	public static final String PROJECT_DELETED = "project deleted";

	//
	public static final String CONFIGURATION_UPDATED = "Configuration Updated successfully.";

	public static final String EXECUTING_PROJECT = "Executing project";

	public static final String PROJECT_CLOSED = "Project was closed successfully";

	public static final String PROJECT_STOPPED = "Stopped project";

	public static final String TOPIC_ADDED = "Added topics successfully";

	public static final String EMPTY_SCRIPT = "Script block must have a valid script";

	// ****************************************EVENTS**************************************************

	public static final String EVENT_TRIGGERED = "Event triggered";

	public static final String EVENT_ADDED = "Event was successfully added ";

	public static final String EVENT_DELETED = "Event was sucessfully deleted ";

	public static final String EVENT_UPDATED = "Event was successfully updated ";
	
	public static final String LOADING_EVENTS = "Loading events .. ";
	
	public static final String LOADING_EVENT = "Loading event ";
	
	public static final String ADDING_EVENT = "Loading event ";
	
	public static final String REGISTERING_EVENT ="Registering event in runner";
	
	public static final String UPDATING_EVENT_TYPE ="Updating event type ";
	
	public static final String UPDATING_EVENT_TYPE_FAILED ="Failed to set event type in runner";

	public static final String DELETING_EVENT =" deleting event";
	
	public static final String DELETING_EVENT_FROM_RUNNER =" deleting event from runner";

	

	

	// ****************************************rules**************************************************

	// rule addition

	public static final String RULE_BUILD_ERROR = "Error while building a rule";

	public static final String RULE_ADDED_SUCCESSFULLY = " Rule added successfully.";

	public static final String FAILED_TO_ADD_RULE = "Failed to add rule.";

	// rule update

	public static final String RULE_UPDATED_SUCCESSFULLY = "Rule updated successfully";

	// rule deletion

	public static final String RULE_DELETED_SUCCESSFULLY = "Rule deleted successfully";

	public static final String FAILED_TO_DELETE_ALL_RULES = "Failed to delete all rules ";
	
	public static final String FAILED_TO_DELETE_SOME_RULES = "Failed to delete the following rules : ";


	public static final String WORKFLOW_DELETED_SUCCESSFULLY = "Workflow deleted successfully";

	public static final String WORKFLOW_UPDATED_SUCCESS = "Workflow updated successfully";

	// rule build


	
	public static final String RULE_WAS_BUILT_SUCCESSFULLY = "Rule was built successfully";

	// class addition
	public static final String CLASS_WAS_ADDED_SUCCESSFULLY = "Class was added successfully";

	// ****************************************WORKFLOW**************************************************
	public static final String CREATED_PROJECT_SUCCESSFULLY = "Created project successfully";

	public static final String BUILT_EVERYTHING_SUCCESSFULLY = " Built everything successfully";


	public static final String ADDED_WORKFLOW_SUCCESSFULLY = "Added workflow successfully";

	public static final String WORKFLOW_BUILT_SUCCESSFULLY = "Workflow built successfully";

	public static final String EXECUTING_WORKFLOW = "Executing workflow";

	public static final String WORKFLOW_DEPLOYED = "Workflow deployed to engine";

	public static final String ADDED_WORKFLOW_COMPONENT_SUCCESSFULLY = "Added workflow component successfully";

	public static final String SEQUENCE_FLOW_DELETED_SUCCESSFULLY = "Sequence flow deleted successfully";

	public static final String BLOCK_DELETED_SUCCESSFULLY = "Block deleted successfully";

	public static final String FRONT_CONFIG = "Saved front config";

	// ****************************************Variables**************************************************

	public static final String VAR_ADDED_SUCCESSFULLY = " Variable added successfully.";

	public static final String VAR_DELETED = "Variable was sucessfully deleted ";

	// ****************************************RuleBuilder**************************************************

	// rules

	public static final String RULE_ID_NULL = "Rule identifier can't be empty";
	public static final String RULE_NAME_NULL = "Rule name can't be empty";
	public static final String PROJECT_ID_NULL = "Project identifier can't be empty";
	public static final String RULE_PROJECT_ID_NULL = "Rule's project id was not found";
	public static final String NO_EXECUTION_BLOCK = "Rule must have at least one execution block";
	public static final String RULE_NOT_FOUND = "No rule with this ID was found";
	public static final String RULE_EXISTS = "a rule with this Id already exists. ";

	// rule blocks
	public static final String ADD_BLOCK_FAIL = "Failed to Add Block";
	public static final String BLOCK_EXISTS = "A block with this id already exists.";
	public static final String BLOCK_NOT_FOUND = "No Block with this id was found";
	public static final String BLOCK_ID_NULL = "A block must have an Id";
	public static final String BLOCK_RULE_ID_NULL = "A block must have a rule Id";
	public static final String BLOCK_PROJECT_ID_NULL = "A block must have a project Id";
	public static final String BLOCK_OPERATION_ID_NULL = "A block operation id can not be empty";
	public static final String BLOCK_OPERATION_ID_UNKNOWN = "Block operation id is unknown.";
	public static final String BLOCK_NAME_EMPTY = "Block name can't be empty";
	public static final String RULE_BUILD_FAILED = "Failed to build rule";
	public static final String UPDATE_BLOCK_FAILED = "Failed to update block";
	public static final String ID_NOT_FOUND = "Failed to read rule id";
	public static final String RULE_CONTAINS_ERRORS = "Rule contains errors";
	public static final String RULE_FILE_NOT_FOUND = "Rule file was not found";
	public static final String INPUT_CONNECTION1 = "Comparison block cannot have ";
	public static final String INPUT_CONNECTION2 = " input connexions.";


	// ****************************************RuleEngine**************************************************

	// building project
	public static final String BUILDING_PROJECT_CONTAINER = "Building Project Container...";

	// rules
	public static final String BUILDING_PROJECT_CONTAINER_SUCCESS = "Project Container built Succesfully";
	public static final String BUILDING_PROJECT_CONTAINER_FAILED = "Project Container build Failed";
	// running project
	public static final String KIE_CONTAINER_UPDATE_FAILED = "Failed to update kie container";
	public static final String PROJECT_CONTAINER_RUNNING = "This project container is already running";
	public static final String STOPPING_PROJECT_CONTAINER = "Stopping project container execution";
	public static final String STOPPING_PROJECT_CONTAINER_SUCCESSFULLY = "Project container stopped.";
	public static final String STOPPING_PROJECT_CONTAINER_FAILED = "Failed to stop project container";
	public static final String UNEXPECTED_ERROR = "An unexpected error occured";
	public static final String RULE_ADDED = "Rule successfully added";
	public static final String RULE_UPDATED = "Successfully updated rule";
	public static final String RULE_UPDATE_FAIL = "Failed to update rule";
	public static final String RULE_DELETED = "Rule deleted from engine";
	public static final String RULE_DELETE_FAIL = "Failed to delete rule";
	public static final String RULE_COMPILED = "Rule compiled successfully";
	public static final String RULE_COMPILATION_FAILED = "Failed to compile Rule";
	public static final String FIRING_ALL_RULES = "Firing all rule..";
	public static final String NO_RULES = "NO_RULES_WERE_FOUND";
	public static final String BUILDING_KIE = "Building kie..";
	public static final String KIE_BUILT = "Kie built sucessfully.";
	public static final String KIE_INIT = "Initialising kieBase..";
	public static final String KIE_INIT_FAILED = "failed to initialise kie base";

	public static final String FAILED_TO_UPDATE_FACT = "Failed to update fact";

	public static final String FAILED_TO_FIRE_RULES = "Failed to fire rules";
	

}
