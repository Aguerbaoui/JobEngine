package io.je.utilities.constants;


public class JEMessages {


    //*****************************************GLobalMessages******************************//
    public static final String BUILDER_STARTED = "Builder started successfully";

    public static final String RUNNER_STARTED = "Runner started successfully";

    public static final String RUNNER_SHUTTING_DOWN = " Runner is shutting down";

    public static final String RUNNER_CONFFIG_UPDATE = "updating JERunner configuration";

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

    public static final String UPDATING_BUILDER_AND_RUNNER_CONFIGURATION = "Updating builder and runner configuration";

    public static final String UPDATING_RUNNER_CONFIGURATION_CONFIG = "Updating runner configuration, config";

    public static final String RUNNER_IS_DOWN_CHECKING_AGAIN_IN_5_SECONDS = "Runner is down, checking again in 5 seconds";

    public static final String RUNNER_IS_UP_UPDATING_NOW = "Runner is up, updating now";

    // ****************************************ClassBuilder**************************************************

    public static final String CLASS_NAME_NULL = "Class name cannot be empty";

    public static final String CLASS_UNKNOWN = "Class Unknown";

    public static final String TYPE_UNKNOWN = "Type Unknown";

    public static final String BUILDING_CLASS = "Building class..";

    public static final String CLASS_BUILD_FAILED = "Failed to build class ";

    public static final String CLASS_LOAD_FAILED = "Class load failed ";

    public static final String INVALID_CLASS_FORMAT = "Class format is not valid, interface can't inherit from class ";

    public static final String UNKNOW_CLASS_TYPE = "Class type cannot be determined. [Class types : Class/Interface/Enum ] ";

    public static final String INHERITED_CLASS_ENUM = "Class cannot inherit from an enumeration";

    public static final String MULTIPLE_INHERITANCE = "Multiple inhertiance is not supported";

    public static final String CLASS_NOT_FOUND = "No Class with this id was found in DataModelRestApi";

    public static final String GETTING_CLASS_DEFINITION = "loading class definiton";

    public static final String LOADING_ALL_CLASSES_FROM_DB = "Loading all classes from db to memory";

    public static final String ADDING_CLASS = "Adding class ";

    public static final String ADDING_CLASS_TO_RUNNER_FROM_BUILDER_WITH_ID = "Adding class to runner from builder with id";

    public static final String FAILED_TO_LOAD_CLASS = "Failed to load class";

    // ****************************************Data Listener**************************************************

    public static final String LISTENING_ON_TOPICS = " Listening on topics : ";

    public static final String ADDING_TOPICS = "Adding topics : ";

    public static final String STOPPED_LISTENING_ON_TOPICS = " Stopping Listening on topics : ";

    public static final String INTERRUPT_TOPIC_ERROR = "Error interrupting thread for topic : ";

    public static final String DATA_RECEIVED = "Data received  ";

    public static final String THREAD_INTERRUPTED = " Thread interrupted while listening to data";

    public static final String INJECTING_DATA = " Injecting data in runner from listener";

    public static final String REMOVING_TOPIC_SUBSCRIPTION = "Removing topic subscription";


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

    public static final String STOPPING_WORKFLOW = "Stopping workflow executions";

    public static final String UPDATING_WF = "Updating workflow";

    public static final String ADDING_BPMN_SCRIPT = " Adding a bpmn script";

    public static final String ADDING_LISTENERS_TO_PROCESS = "Adding listeners to process";

    public static final String PROCESSING_BLOCK_NAME = "Processing block name";

    public static final String SAVING_BPMN_FILE_TO_PATH = "Saving bpmn file to path";

    public static final String DEPLOYING_IN_RUNNER_WORKFLOW_WITH_ID = "Deploying in runner workflow wit" +
            "h id ";
    public static final String FAILED_TO_DEPLOY_IN_RUNNER_WORKFLOW_WITH_ID = "Failed to deploy in runner workflow with id";

    public static final String TRANSITIONING_FROM = "Transitioning from";

    public static final String GATEWAY_ID = "Gateway id";

    public static final String JUST_EXECUTED = "just executed";
    public static final String TASK_ID = "Task id";

    public static final String PROCESS_HAS_TO_BE_TRIGGERED_BY_EVENT = "Process has to be triggered by event";

    public static final String RUNNING_ALL_WORKFLOWS_IN_PROJECT_ID = "Running all workflows in project id";

    public static final String BUILDING_WORKFLOWS_IN_PROJECT = " Building workflows in project";

    public static final String ERROR_DELETING_A_NON_EXISTING_PROCESS = "Error deleting a non existing process";

    public static final String EXECUTING_WEB_API_TASK = "Executing web api task";

    public static final String NETWORK_CALL_RESPONSE_IN_WEB_SERVICE_TASK = "Network call response in web service task";

    public static final String DEPLOYING_WF = "Deploying workflow";

    public static final String DB_TASK = "Outsourcing database operation in task = ";

    public static final String MAIL_SERVICE_TASK_RESPONSE = " Mail service task response";

    public static final String UPDATING_A_WORKFLOW_BLOCK_WITH_ID = "Updating a workflow block with id ";

    public static final String INFORM_FROM_USER = "Inform from user";

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

    public static final String VALIDATING_RULE = "Checking rule validity";

    public static final String ADD_INSTANCE_FAILED = "failed to create instance";
    //events

    public static final String FOUND_EVENT = " Found event with id = ";

    public static final String UPDATING_EVENT = " Found event with id = ";

    public static final String REMOVING_EVENTS = "Updating fact";

    public static final String TRIGGERING_NOW = " Triggering now";

    public static final String REMOVING_EVENT = " Removing now";

    public static final String ERROR_DELETING_EVENT = "Error deleting event";


    // ****************************************ResponseMessages**************************************************


    public static final String DELETE_WORKFLOW_FAILED = "Error deleting  workflow";

    public static final String RUN_WORKFLOW_FAILED = "Error running workflow in runner : ";

    public static final String PROJECT_RUN_FAILED = "Failed to run project";

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

    public static final String JERUNNER_ERROR = "JERunner Error";

    public static final String DATA_DEFINITION_CLASS_NOT_FOUND = "No class with this Id was found";

    public static final String PROJECT_RUNNING = "Project already running";

    public static final String PROJECT_BUILT = "Project already built";

    public static final String PROJECT_NOT_BUILT = "Project needs build";

    public static final String PROJECT_ALREADY_STOPPED = "Project already stopped";

    public static final String WORKFLOW_ALREADY_RUNNING = "Workflow already running";

    public static final String WORKFLOW_NEEDS_BUILD = "Workflow needs build";

    public static final String EVENT_NOT_FOUND = " No event with this id was found ";

    public static final String NOT_ALPHABETICAL = " Should only contain alphabetical characters ";

    public static final String WORKFLOW_TRIGGERED_BY_EVENT = " Workflow can't start manually";

    public static final String EVENT_ALREADY_EXISTS = "An event with this id already exists";

    public static final String MISSING_CONFIG = "Application configuration is missing. Make sure to properly configure the application before proceeding.";

    public static final String JEBUILDER_UNREACHABLE = " JEBuilder unreachable";

    // response messages
    public static final String PROJECT_EXISTS = "Project exists";

    public static final String PROJECT_DELETED = "Project deleted";

    //
    public static final String CONFIGURATION_UPDATED = "Configuration updated successfully.";

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

    public static final String ADDING_EVENT = "Adding event ";

    public static final String REGISTERING_EVENT = "Registering event in runner";

    public static final String UPDATING_EVENT_TYPE = "Updating event type ";

    public static final String UPDATING_EVENT_TYPE_FAILED = "Failed to set event type in runner";

    public static final String DELETING_EVENT = " deleting event";

    public static final String DELETING_EVENT_FROM_RUNNER = " deleting event from runner";

    public static final String UPDATING_EVENT_TYPE_IN_RUNNER = "Updating event type in runner";


    // ****************************************rules**************************************************

    // rule addition

    public static final String RULE_BUILD_ERROR = "Error while building a rule";

    public static final String RULE_ADDED_SUCCESSFULLY = " Rule added successfully.";

    public static final String FAILED_TO_ADD_RULE = "Failed to add rule.";
    
    public static final String RULE_EXECUTION_ERROR = "RULE EXECUTION ERROR : ";



    // rule deletion


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

    public static final String BUILDING_BPMN_FROM_JEWORKFLOW = "Building bpmn from jeworkflow id =";


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


    // ****************************************NETWORKMSGS**************************************************

    public static final String NETWORK_GET = " Making Get network call to url = ";

    public static final String NETWORK_DELETE = " Making Delete network call to url = ";

    public static final String NETWORK_POST = " Making POST network call to url = ";

    public static final String NETWORK_CALL_ERROR = "Error making network call for url = ";

    public static final String NETWORK_DELETE_EVENT = "Sending delete event request to runner,";

    public static final String NETWORK_CLEAN_PROJECT = "Sending clean project request";

    public static final String NETWORK_DELETE_WF = "Sending delete workflow request to runner,";

    public static final String NETWORK_ADD_VAR = "Sending add variable request to runner,";

    public static final String NETWORK_DELETE_VAR = "Sending delete variable request to runner,";

    public static final String NETWORK_UPDATE_EVENT = "Sending update event request to runner";


    // ****************************************UTILS**************************************************
    public static final String READING_FILE = " Reading file  ";

    public static final String DELETE_FILE_FAILED = " Failed deleting file ";

    public static final String SETTING_DATA_DEFINITION_URL_FROM_CONTROLLER = "Setting data definition url from controller";

    public static final String SENDING_REQUEST_TO_DATA_MODEL = "Sending request to Data Model";

    public static final String NO_RESPONSE_FROM_DATA_MODEL = "No Response from Data Model";

    public static final String UPDATING_CONFIGURATION = " Updating configuration";

}
