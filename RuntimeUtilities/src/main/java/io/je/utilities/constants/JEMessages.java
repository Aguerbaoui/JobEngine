package io.je.utilities.constants;


public class JEMessages {


    //*****************************************GLobalMessages******************************//
    public static final String BUILDER_STARTED = "Builder started successfully";

    public static final String RUNNER_STARTED = "Runner started successfully";

    public static final String MONITOR_STARTED = "Monitor started successfully";

    public static final String RUNNER_SHUTTING_DOWN = "Runner is shutting down";

    public static final String RUNNER_CONFFIG_UPDATE = "Updating JERunner configuration";

    public static final String SETTING_ASYNC_EXECUTOR = "Setting async executor parameters";

    public static final String LOGGER_INITIALIZED = "Logger initialized";

    public static final String INITILIZING_BUILDER = "Initializing JEBuilder";


    //*****************************************Configuration******************************//

    public static final String DATA_DEFINITION_URL_MISSING = "Data definition URL is missing";

    public static final String DATA_MODEL_URL_MISSING = "Data Manager URL is missing";

    public static final String JERUNNER_URL_MISSING = "JERunner URL is missing";

    public static final String DATA_MODEL_SUB_PORT_MISSING = "Subscriber port is missing";

    public static final String DATA_MODEL_REQ_PORT_MISSING = "Request port is missing";

    public static final String DROOLS_DATE_FORMAT_MISSING = "The job engine's timedate format is not specified";

    public static final String UPDATING_BUILDER_AND_RUNNER_CONFIGURATION = "Updating JEBuilder and JERunner configuration";

    public static final String UPDATING_RUNNER_CONFIGURATION_CONFIG = "Updating JERunner configuration";

    public static final String RUNNER_IS_DOWN_CHECKING_AGAIN_IN_5_SECONDS = "Runner is down, rechecking in 5  seconds";

    public static final String DATABASE_IS_DOWN_CHECKING_AGAIN = "Database is down, rechecking";

    public static final String DATABASE_IS_DOWN = "Database is down, please check the database service";

    public static final String RUNNER_IS_UP_UPDATING_NOW = "Runner is up, updating now";

    // ****************************************ClassBuilder**************************************************

    public static final String CLASS_NAME_NULL = "Class name cannot be empty";

    public static final String CLASS_UNKNOWN = "Class unknown";

    public static final String CLASS_NOT_LOADED = "Loaded classes list does not recognize this Id: ";

    public static final String TYPE_UNKNOWN = "Type unknown";

    public static final String BUILDING_CLASS = "Building class ";

    public static final String CLASS_BUILD_FAILED = "Failed to build class ";

    public static final String CLASS_LOAD_FAILED = "Class load failed ";

    public static final String CLASS_LOAD_IN_RUNNER_FAILED = "Class load failed in runner";

    public static final String CLASS_LOAD_DENIED_ACCESS = "Class load failed due to denied access";

    public static final String INVALID_CLASS_FORMAT = "Class format is not valid, interface cannot inherit from class ";

    public static final String UNKNOW_CLASS_TYPE = "Class type cannot be determined. [Class types:  Class/Interface/Enum ] ";

    public static final String INHERITED_CLASS_ENUM = "Class cannot inherit from an enumeration";

    public static final String MULTIPLE_INHERITANCE = "Multiple inheritance is not supported";

    public static final String CLASS_NOT_FOUND = "No class matching this Id was found in DataModelRestApi";

    public static final String GETTING_CLASS_DEFINITION = "Loading class definition";

    public static final String LOADING_ALL_CLASSES_FROM_DB = "Loading all classes from database to memory";

    public static final String ADDING_CLASS = "Adding class ";

    public static final String UPDATING_CLASS = "Updating class ";

    public static final String ADDING_CLASS_TO_RUNNER_FROM_BUILDER_WITH_ID = "Adding class to JERunner from JEBuilder with Id";

    public static final String ADDING_CLASS_TO_RUNNER_FROM_BUILDER = "Adding class to JERunner from JEBuilder";


    public static final String FAILED_TO_LOAD_CLASS = "Failed to load class";

    public static final String CLASS_ALREADY_EXISTS = "This class already exists.";

    // ****************************************Data Listener**************************************************

    public static final String LAUNCHING_LISTENING_TO_TOPIC = "Launching listening to topic : ";

    public static final String ADDING_TOPICS = "Adding topics: ";

    public static final String STOPPING_LISTENING_TO_TOPIC = "Stopping listening to topic : ";

    public static final String INTERRUPT_TOPIC_ERROR = "Error interrupting thread for topics : ";

    public static final String DATA_RECEIVED = "Data received : ";

    public static final String THREAD_INTERRUPTED = "Thread interrupted while listening to data";

    public static final String INJECTING_DATA = "Pushing data in JERunner from listener";

    public static final String REMOVING_TOPIC_SUBSCRIPTION = "Removing topic subscription";

    public static final String CLOSING_SOCKET = "Closing socket";

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

    public static final String LOADING_PROJECTS = "Loading all projects from database ";

    public static final String RESETTING_PROJECTS = "Resetting projects after updating JERunner";

    //WF
    public static final String LOADING_WFS = "Getting all workflows";

    public static final String LOADING_WF = "Getting workflow";

    public static final String ADDING_WF = "Adding workflow";

    public static final String REMOVING_WF = "Removing workflow";

    public static final String REMOVING_WFS = "Removing workflows";

    public static final String ADDING_WF_BLOCK = "Adding a workflow block";

    public static final String DELETING_WF_BLOCK = "Deleting a workflow block";

    public static final String ADDING_SEQUENCE_FLOW = "Adding a sequence flow";

    public static final String DELETING_SEQUENCE_FLOW = "Deleting a sequence flow";

    public static final String BUILDING_WF = "Building workflow";

    public static final String BUILDING_WFS = "Building workflows";

    public static final String RUNNING_WF = "Running workflow";

    public static final String RUNNING_WFS = "Running workflows";

    public static final String STOPPING_WORKFLOW = "Stopping workflow execution";

    public static final String UPDATING_WF = "Updating workflow";

    public static final String ADDING_BPMN_SCRIPT = "Adding a JE script";

    public static final String ADDING_LISTENERS_TO_PROCESS = "Adding listeners to process";

    public static final String PROCESSING_BLOCK_NAME = "Processing block name";

    public static final String SAVING_BPMN_FILE_TO_PATH = "Saving BPMP file to path";

    public static final String DEPLOYING_IN_RUNNER_WORKFLOW_WITH_ID = "Deploying the workflow with Id in JERunner ";
    public static final String FAILED_TO_DEPLOY_IN_RUNNER_WORKFLOW_WITH_ID = "Failed to deploy the workflow with Id in JERunner ";

    public static final String TRANSITIONING_FROM = "Transitioning from";

    public static final String GATEWAY_ID = "Gateway Id";

    public static final String JUST_EXECUTED = "just executed";

    public static final String TASK_ID = "Task Id";

    public static final String PROCESS_HAS_TO_BE_TRIGGERED_BY_EVENT = "Process has to be triggered by event or following a specific schedule";

    public static final String PROCESS_EXITED = "Process has ended its execution";

    public static final String RUNNING_ALL_WORKFLOWS_IN_PROJECT_ID = "Running all workflows in project Id";

    public static final String BUILDING_WORKFLOWS_IN_PROJECT = "Building workflows in project";

    public static final String ERROR_DELETING_A_NON_EXISTING_PROCESS = "Error deleting a non-existing process";

    public static final String EXECUTING_WEB_API_TASK = "Executing web API task";

    public static final String NETWORK_CALL_RESPONSE_IN_WEB_SERVICE_TASK = "Network call response in web API task";

    public static final String DEPLOYING_WF = "Deploying workflow";

    public static final String DB_TASK = "Executing database operation in task = ";

    public static final String MAIL_SERVICE_TASK_RESPONSE = "Email service task response";

    public static final String EMAIL_SENT_SUCCESSFULLY = "Email sent successfully";
    public static final String DB_SERVICE_TASK_RESPONSE = "Database service task response";

    public static final String DB_API_RESPONSE = "Database API response";

    public static final String UPDATING_A_WORKFLOW_BLOCK_WITH_ID = "Updating a workflow block with Id ";

    public static final String INFORM_FROM_USER = "Inform from user";

    //Rules
    public static final String ADDING_SCRIPTED_RULE = "Adding new scripted rule";

    public static final String UPDATING_SCRIPTED_RULE = "Updating new scripted rule";

    public static final String LOADING_RULES = "Getting all rules";

    public static final String ADDING_RULE = "Adding rule";

    public static final String UPDATING_RULE = "Updating rule";

    public static final String COMPILING_RULE = "Compiling rule";

    public static final String COMPILING_ALL_RULES = "Compiling all rules";

    public static final String LOADING_RULE = "Getting rule";

    public static final String ADDING_BLOCK = "Adding block ";

    public static final String UPDATING_BLOCK = "Updating block ";

    public static final String DELETING_RULE = "Deleting rule";

    public static final String DELETING_RULES = "Deleting rules";

    public static final String DELETING_RULE_RUNNER = "Deleting rule from JERunner";

    public static final String DELETING_BLOCK = "Deleting block ";

    public static final String BUILDING_RULES = "Building all rules";

    public static final String BUILDING_RULE = "Building rule";

    public static final String SENDNG_RULE_TO_RUNNER = "Sending rule build request to JERunner";

    public static final String UPDATING_FACT = "Updating fact";

    public static final String VALIDATING_RULE = "Checking rule validity";

    //events

    public static final String FOUND_EVENT = "Found event with Id = ";

    public static final String UPDATING_EVENT = "Found event with Id = ";

    public static final String REMOVING_EVENTS = "Updating fact";

    public static final String TRIGGERING_NOW = "Triggering event  now";

    public static final String REMOVING_EVENT = "Removing event now";

    public static final String ERROR_DELETING_EVENT = "Error deleting event";

    public static final String FAILED_TO_ADD_EVENT = "Failed to add event";


    // ****************************************ResponseMessages**************************************************


    public static final String DELETE_WORKFLOW_FAILED = "Error deleting workflow : ";

    public static final String RUN_WORKFLOW_FAILED = "Error running workflow in JERunner: ";

    public static final String PROJECT_RUN_FAILED = "Failed to run project";

    public static final String WORKFLOW_NOT_FOUND = "Workflow not found";

    public static final String WORKFLOW_BLOCK_NOT_FOUND = "Workflow block not found";

    public static final String WORKFLOW_START_BLOCK_NOT_DEFINED = "Workflow Start block not defined";
    public static final String WORKFLOW_START_BLOCK_NOT_UNIQUE = "Workflow Start block not unique";
    public static final String WORKFLOW_END_BLOCK_NOT_DEFINED = "Workflow End block not defined";

    public static final String WORKFLOW_END_BLOCK_NOT_UNIQUE = "Workflow End block not unique";

    public static final String PROJECT_NOT_FOUND = "No project with this Id was found.";

    public static final String PROJECT_LOAD_ERROR = "Error loading project files : ";

    public static final String UKNOWN_ERROR = "Unknown error : ";

    public static final String INVALID_SEQUENCE_FLOW = "Invalid sequence flow";

    public static final String NETWORK_ERROR = "Error connecting to runtime manager API";

    public static final String DATA_LISTENER_NOT_FOUND = "Data listener not found";

    public static final String DATA_DEFINITION_API_UNREACHABLE = "Data definition model unreachable";

    public static final String DATA_DEFINITION_API_ERROR = "Data definition model error";

    public static final String JERUNNER_UNREACHABLE = "JERunner unreachable";

    public static final String JERUNNER_ERROR = "JERunner error";

    public static final String DATA_DEFINITION_CLASS_NOT_FOUND = "No class with this Id was found";

    public static final String PROJECT_RUNNING = "Project already running";

    public static final String PROJECT_BUILT = "Project already built";

    public static final String PROJECT_NOT_BUILT = "Project needs to be built";

    public static final String PROJECT_ALREADY_STOPPED = "Project already stopped";

    public static final String WORKFLOW_ALREADY_RUNNING = "Workflow already running";

    public static final String WORKFLOW_NEEDS_BUILD = "Workflow needs to be built";

    public static final String EVENT_NOT_FOUND = "No event with this Id was found ";

    public static final String ERROR_SETTING_EVENT = "Error while setting event in Runner  ";

    public static final String NOT_ALPHABETICAL = "Must contain alphabetical characters only ";

    public static final String WORKFLOW_TRIGGERED_BY_EVENT = "Workflow cannot be started manually";

    public static final String EVENT_ALREADY_EXISTS = "An event with this name already exists";


    public static final String JEBUILDER_UNREACHABLE = "JEBuilder unreachable";

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
    public static final String EMPTY_CODE = "Code is empty";

    // ****************************************EVENTS**************************************************

    public static final String EVENT_TRIGGERED = "Event triggered";

    public static final String EVENT_ADDED = "Event was successfully added ";

    public static final String EVENT_DELETED = "Event was successfully deleted ";

    public static final String EVENTS_DELETED = "Event were successfully deleted ";

    public static final String EVENT_UPDATED = "Event was successfully updated ";

    public static final String LOADING_EVENTS = "Loading events ... ";

    public static final String LOADING_EVENT = "Loading event ";

    public static final String ADDING_EVENT = "Adding event ";

    public static final String REGISTERING_EVENT = "Registering event in JERunner";

    public static final String UPDATING_EVENT_TYPE = "Updating event type ";

    public static final String UPDATING_EVENT_TYPE_FAILED = "Failed to set event type in JERunner";

    public static final String DELETING_EVENT = "deleting event";

    public static final String DELETING_EVENTS = "deleting events";

    public static final String DELETING_EVENT_FROM_RUNNER = "deleting event from JERunner";

    public static final String UPDATING_EVENT_TYPE_IN_RUNNER = "Updating event type in JERunner";


    // ****************************************rules**************************************************

    // rule addition

    public static final String RULE_BUILD_ERROR = "Error while building a rule";

    public static final String RULE_ADDED_SUCCESSFULLY = "Rule added successfully.";

    public static final String FAILED_TO_ADD_RULE = "Failed to add rule : ";


    // rule deletion


    public static final String FAILED_TO_DELETE_ALL_RULES = "Failed to delete all rules ";

    public static final String FAILED_TO_DELETE_SOME_RULES = "Failed to delete the following rules:  ";


    public static final String WORKFLOW_DELETED_SUCCESSFULLY = "Workflow deleted successfully";

    public static final String WORKFLOW_UPDATED_SUCCESS = "Workflow updated successfully";

    // rule build


    public static final String RULE_WAS_BUILT_SUCCESSFULLY = "Rule was built successfully";

    // class addition
    public static final String CLASS_WAS_ADDED_SUCCESSFULLY = "Class was added successfully";

    // ****************************************WORKFLOW**************************************************
    public static final String CREATED_PROJECT_SUCCESSFULLY = "Created project successfully";

    public static final String BUILT_EVERYTHING_SUCCESSFULLY = "Built everything successfully";

    public static final String BUILDING_BPMN_FROM_JEWORKFLOW = "Building BPMN from JE workflow Id =";


    public static final String ADDED_WORKFLOW_SUCCESSFULLY = "Workflow added successfully";

    public static final String WORKFLOW_BUILT_SUCCESSFULLY = "Workflow built successfully";

    public static final String EXECUTING_WORKFLOW = "Executing workflow";

    public static final String WORKFLOW_DEPLOYED = "Workflow deployed to engine ";

    public static final String ADDED_WORKFLOW_COMPONENT_SUCCESSFULLY = "Added workflow component successfully";

    public static final String SEQUENCE_FLOW_DELETED_SUCCESSFULLY = "Sequence flow deleted successfully";

    public static final String BLOCK_DELETED_SUCCESSFULLY = "Block deleted successfully";

    public static final String FRONT_CONFIG = "Saved front configuration";

    // ****************************************Variables**************************************************

    public static final String VAR_ADDED_SUCCESSFULLY = "Variable added successfully.";

    public static final String VAR_DELETED = "Variable was deleted successfully  ";

    public static final String VARS_DELETED = "Variables were successfully deleted ";

    public static final String ADDING_VARIABLE = "Adding variable ";

    public static final String REMOVING_VARIABLE = "Removing variable ";

    public static final String UPDATING_VARIABLE = "Updating variable ";

    public static final String UPDATING_VARIABLE_FAILED = "Failed to update variable. ";


    // ****************************************RuleBuilder**************************************************

    // rules

    public static final String RULE_ID_NULL = "Rule Identifier cannot be empty";

    public static final String RULE_NAME_NULL = "Rule name cannot be empty";

    public static final String PROJECT_ID_NULL = "Project Identifier cannot be empty";

    public static final String RULE_PROJECT_ID_NULL = "Rule's project Id was not found";

    public static final String NO_EXECUTION_BLOCK = "Rule must have at least one function block";

    public static final String RULE_NOT_FOUND = "No rule with this Id was found";

    public static final String RULE_EXISTS = "A rule with this Id already exists. ";

    // rule blocks
    public static final String ADD_BLOCK_FAIL = "Failed to add block";

    public static final String BLOCK_NAME_EXISTS = "A block with this name already exists.";

    public static final String BLOCK_EXISTS = "A block with this Id already exists.";

    public static final String BLOCK_NOT_FOUND = "No block with this Id was found";

    public static final String BLOCK_ID_NULL = "A block must have an Id";

    public static final String BLOCK_RULE_ID_NULL = "A block must have a rule Id";

    public static final String BLOCK_PROJECT_ID_NULL = "A block must have a project Id";

    public static final String BLOCK_OPERATION_ID_NULL = "A block operation Id cannot be empty";

    public static final String BLOCK_OPERATION_ID_UNKNOWN = "Block operation Id is unknown.";

    public static final String BLOCK_NAME_EMPTY = "Block name cannot be empty";

    public static final String RULE_BUILD_FAILED = "Failed to build rule";

    public static final String UPDATE_BLOCK_FAILED = "Failed to update block";

    public static final String ID_NOT_FOUND = "Failed to read rule Id";

    public static final String RULE_CONTAINS_ERRORS = "Rule contains errors";

    public static final String RULE_FILE_NOT_FOUND = "Rule file was not found : ";

    public static final String INPUT_CONNECTION1 = "Comparison block cannot have ";

    public static final String INPUT_CONNECTION2 = "input connections.";


    // ****************************************RuleEngine**************************************************

    // building project
    public static final String BUILDING_PROJECT_CONTAINER = "Building project container...";

    // rules
    public static final String BUILDING_PROJECT_CONTAINER_SUCCESS = "Project container built successfully";

    public static final String BUILDING_PROJECT_CONTAINER_FAILED = "Project container build failed";

    // running project
    public static final String KIE_CONTAINER_UPDATE_FAILED = "Failed to update project container";

    public static final String PROJECT_CONTAINER_RUNNING = "This project container is already running";

    public static final String STOPPING_PROJECT_CONTAINER = "Stopping project container execution";

    public static final String STOPPING_PROJECT_CONTAINER_SUCCESSFULLY = "Project container stopped";

    public static final String STOPPING_PROJECT_CONTAINER_FAILED = "Failed to stop project container";

    public static final String RELOADING_PROJECT_CONTAINER = "Reloading project container ... ";

    public static final String UNEXPECTED_ERROR = "An unexpected error occurred : ";

    public static final String RULE_ADDED = "Rule added successfully ";

    public static final String RULE_UPDATED = "Rule updated successfully";

    public static final String RULE_UPDATE_FAIL = "Failed to update rule";

    public static final String RULE_DELETED = "Rule deleted from rule engine successfully";

    public static final String RULE_DELETE_FAIL = "Failed to delete rule";

    public static final String RULE_COMPILED = "Rule compiled successfully";

    public static final String RULE_COMPILATION_FAILED = "Failed to compile Rule";

    public static final String FIRING_ALL_RULES = "Firing all rules";

    public static final String NO_RULES = "No rule was found";

    public static final String BUILDING_KIE = "Building knowledge base";

    public static final String KIE_BUILT = "Knowledge base built successfully";

    public static final String KIE_INIT = "Initializing knowledge base";

    public static final String KIE_INIT_FAILED = "Failed to initialize knowledge base";

    public static final String FAILED_TO_UPDATE_FACT = "Failed to update fact";

    public static final String FAILED_TO_FIRE_RULES = "Failed to fire rules";


    // ****************************************NETWORKMSGS**************************************************

    public static final String NETWORK_GET = "Making Get network call to URL = ";

    public static final String NETWORK_DELETE = "Making Delete network call to URL = ";

    public static final String NETWORK_POST = "Making POST network call to url = ";

    public static final String NETWORK_CALL_ERROR = "Error making network call for URL = ";

    public static final String NETWORK_DELETE_EVENT = "Sending delete event request to JERunner : ";

    public static final String NETWORK_CLEAN_PROJECT = "Sending clean project request : ";

    public static final String NETWORK_DELETE_WF = "Sending delete workflow request to JERunner : ";

    public static final String NETWORK_ADD_VAR = "Sending add variable request to JERunner : ";

    public static final String NETWORK_DELETE_VAR = "Sending delete variable request to JERunner : ";

    public static final String NETWORK_UPDATE_EVENT = "Sending update event request to JERunner : ";


    // ****************************************UTILS**************************************************
    public static final String READING_FILE = "Reading file";

    public static final String DELETE_FILE_FAILED = "Failed to delete file ";

    public static final String SETTING_DATA_DEFINITION_URL_FROM_CONTROLLER = "Setting data definition URL from controller";

    public static final String SENDING_REQUEST_TO_DATA_MODEL = "Sending request to Data Model";

    public static final String NO_RESPONSE_FROM_DATA_MODEL = "No response from Data Model";

    public static final String UPDATING_CONFIGURATION = "Updating configuration";


//******************************************************* updates ****************************************

    public static final String ADDING_JAR_TO_PROJECT = "Adding jar to project";
    public static final String ADDING_FILE_TO_PROJECT = "Adding file to project";
    public static final String ADDING_JAR_FILE_TO_RUNNER = "Adding jar file to JERunner ";
    public static final String CLASS_COMPILATION_FAILED = "Class compilation failed ";
    public static final String DATA_MODEL_DATE_FORMAT_MISSING = "Data Model timedate format is not specified";
    public static final String VARIABLE_NOT_FOUND = "No variable with this Id was found: ";
    public static final String VARIABLE_EXISTS = "Variable already exists";
    public static final String STOPPING_WF = "Stopping workflow";
    public static final String LOGGING_SYSTEM_URL_MISSING = "Tracker URL is not configured";
    public static final String LOGGING_SYSTEM_PORT_MISSING = "Tracker port is not configured";
    public static final String EMAIL_API_URL_MISSING = "Email API URL is not configured";
    public static final String LOADING_VARIABLES = "Loading variables...";
    public static final String PROJECT_AUTO_RELOAD = "Setting project auto reload to";
    public static final String PROJECT_UPDATED = "Project updated successfully";
    public static final String RULE_EXECUTION_ERROR = "Rule execution error : ";
    public static final String ERROR_DELETING_A_PROCESS = "Error deleting a process : ";
    public static final String INFORM_BLOCK_ERROR = "Failed to execute Inform block";
    public static final String EMAIL_BLOCK_ERROR = "Failed to execute Email block";
    public static final String SMS_BLOCK_ERROR = "Failed to execute SMS block";
    public static final String ERROR_OCCURRED_WHEN_SENDING_MESSAGE_TO = "Error occurred when sending message to ";
    public static final String SENT_MESSAGE_SUCCESSFULLY_TO = "Sent message successfully to: ";
    public static final String BLOCK_NAME_CAN_T_BE_UPDATED_BECAUSE_IT_ALREADY_EXISTS = "Block name cannot be updated because its name already exists";
    public static final String WORKFLOW_BUILD_ERROR = "Error while deploying the workflow ";
    public static final String WORKFLOW_RUN_ERROR = "Error while running the workflow ";
    public static final String UPLOADED_JAR_TO_PATH = "Uploaded file to path ";
    public static final String SENDING_VARIABLE_TO_RUNNER = "Sending variable to JERunner";
    public static final String DELETING_VARIABLE = "Deleting variable ";
    public static final String ERROR_ADDING_VARIABLE_TO_PROJECT = "Error while adding variable to project";
    public static final String DELETING_VARIABLES = "Deleting variables ";
    public static final String ERROR_REMOVING_LIBRARY = "Error while removing the library";
    public static final String ERROR_REMOVING_METHOD = "Error while removing the procedure";
    public static final String ERROR_WRITING_VALUE_TO_VARIABLE = "Error while writing value to variable";
    public static final String ERROR_DELETING_VARIABLE_FROM_PROJECT = "Error while deleting variable from project";
    public static final String ERROR_TRIGGERING_EVENT = "Error while triggering event";
    public static final String ERROR_REMOVING_RULE = "Error while removing rule";
    public static final String ERROR_EXECUTING_DB_QUERY = "Error while executing the database query";
    public static final String ERROR_GETTING_CLASS_UPDATES = "Error while getting class updates";
    public static final String FAILED_TO_SEND_LOG_MESSAGE_TO_THE_LOGGING_SYSTEM = "Failed to send log message to the Tracker module: ";
    public static final String FAILED_TO_SEND_MONITORING_MESSAGE_TO_THE_LOGGING_SYSTEM = "Failed to send monitoring data to the JEMonitor module: ";
    public static final String CHECK_DM_FOR_DETAILS = "Check Integration Objects' SIOTH Data Model service's log file for more details. ";
    public static final String ERROR_RUNNING_PROJECT = "Error while running project, check your configuration : ";
    public static final String ERROR_STOPPING_PROJECT = "Error while stopping project, check your configuration ";
    public static final String ERROR_STOPPING_WORKFLOW = "Error while stopping workflow, check your  ";
    public static final String ERROR_WHILE_REFERENCING_A_DISABLED_WORKFLOW = "Error while referencing a disabled workflow: ";
    public static final String WORKFLOW_IS_DISABLED = "The workflow is disabled, check your configuration ";
    public static final String PROCEDURE_DELETED_SUCCESSFULLY = "The procedure was deleted successfully ";
    public static final String LIBRARY_DELETED_SUCCESSFULLY = "The library was deleted successfully ";
    public static final String LIBRARY_EXISTS = "The file already exists in the system ";
    public static final String THREAD_INTERRUPTED_WHILE_EXECUTING = "Thread execution interrupted ";


    //********************************************************* instances ******************************************************
    public static final String WRITE_INSTANCE_FAILED = "Failed to update instance value. ";

    public static final String READ_INSTANCE_FAILED = "Failed to read last values for instance: ";

    public static final String UPDATING_INSTANCE_VALUE = "Updating instance value...";

    public static final String INSTANCE_UPDATE_SUCCESS = "Instance was updated successfully";

    public static final String ADD_INSTANCE_FAILED = "Failed to create instance: ";

    public static final String FAILED_TO_INJECT_DATA = "Failed to inject data: ";

    public static final String DATA_LISTENTING_STARTED = "Started listening for data... ";

    public static final String ERROR_IMPORTING_FILE = "Error while importing file ";

    public static final String PROCEDURE_ADDED_SUCCESSFULLY = "Procedure was added successfully";

    public static final String METHOD_EXISTS = "Procedure with the same name already exists";

    public static final String METHOD_MISSING = "Procedure with this name does not exist";

    public static final String ERROR_DURING_BUILD = "Error occurred while building project";

    public static final String INVALID_CONFIG = "Invalid configuration";

    public static final String GENERATED_RULE = "Generated DRL: ";

    public static final String RULE_NOT_BUILT = "Rule is not built";
    public static final String FAILED_TO_STOP_RULE = "Failed to stop rule : ";
    public static final String STATUS_UPDATE_FAILED = "Failed to update status : ";

    public static final String FAILED_TO_DELETE_RULE = "Failed to delete rule : ";
    public static final String FAILED_TO_DELETE_SUBRULE = "Failed to delete sub rule";
    public static final String FAILED_TO_STOP_THE_WORKFLOW_BECAUSE_IT_ALREADY_IS_STOPPED = "Failed to stop the workflow because it already is stopped";
    public static final String STOPPING_WORKFLOW_FORCED = "User stopped workflow execution";
    public static final String JOB_ENGINE_ACCEPTS_JAR_FILES_ONLY = "Job engine accepts jar files only";
    public static final String FILE_TOO_LARGE = "Jar file size is too large";
    public static final String FAILED_INIT_DATAMODEL = "Failed to read initial values from Data Model. Topic = ";
    public static final String RULE_DISABLED = "Rule is disabled";

    public static final String RULE_ALREADY_STOPPED = "Rule already stopped";
    public static final String RULE_STOPPED = "Rule is stopped";

    public static final String RULE_ALREADY_RUNNING = "Rule is already running";
    public static final String DATAMODELAPI_UNREACHABLE = "Data Model RESTAPI unreachable";
    public static final String DMAPI_RESPONSE = "Data Model definition returned : ";

    public static final String CUSTOM_COMPILATION_SUCCESS = "Compilation in JEClassLoader succeeded.";
    public static final String SENDING_WORKFLOW_MONITORING_DATA_TO_JEMONITOR = "Sending workflow Monitoring data to JEMonitor: ";
    public static final String STARTED_LISTENING_FOR_MONITORING_DATA_FROM_THE_JOB_ENGINE = "Started listening for monitoring data from the JobEngine";

    public static final String CODE_COMPILATION_SUCCESSFUL = "Code was successfully compiled";
    public static final String COMMAND_EXECUTION_FAILED = "Failed to run command";
    public static final String PROCEDURE_SHOULD_CONTAIN_CODE = "Procedure should contain code";
    public static final String FAILED_TO_DELETE_FILES = "Failed to delete files : ";

    public static final String FAILED_TO_LOAD_PROJECT = "Failed to load project from database";
    public static final String ILLEGAL_OPERATION_CLASS_UPDATE_DURING_PROJECT_RUN = "Class updates will only be effective once the project is stopped. Rules using the updated class may be corrupted";

    public static final String READ_INSTANCE_VALUE_FAILED = "Failed to read instance from DataModel service. ";

    public static final String WORKFLOW_STOPPED_SUCCESSFULLY = "Workflow stopped successfully";
    public static final String ERROR_STOPPING_PROCESS = "Error stopping process by pid";
    public static final String ERROR_SENDING_INFORM_MESSAGE = "Error sending inform message to user";
    public static final String SUCCESSFULLY_INFORMED = "Successfully sent inform message";

    public static final String ERROR_BUILDING_JAR_FILE_AFTER_COMPILING_CLASSES_CHECK_ONGOING_PROCESSES = "Error building jar file after compiling classes, check ongoing processes";

    public static final String RULE_RUNNING = "Rule is running";

    public static final String FAILED_TO_CAST_DATA_CHECK_THE_TYPE_OF_THE_VARIABLE_AND_THE_INCOMING_DATA = "Failed to cast data check the type of the variable and the incoming data";

    public static final String ZMQ_REQUEST_RECEIVED = "Received ZMQ request : ";
    public static final String ZMQ_RESPONSE_STARTED = "Started ZMQ responser on address : ";
    public static final String ZMQ_RESPONSE_START_FAIL = "Failed to start ZMQ Responser ";

    public static final String ZMQ_SENDING_RESPONSE = "Sending response : ";
    public static final String ZMQ_FAILED_TO_RESPOND = "Failed to send ZMQ response : ";
    public static final String UNKNOWN_REQUEST = "Unknown Request";
    public static final String EVENT_TRIGGER_FAIL = "Failed to trigger event";


    //********************************************************* Rule blocks ******************************************************
    public static final String THE_BLOCK_IS_NOT_CONFIGURED_PROPERLY = " : The block is not configured properly : ";
    public static final String SINGLE_INPUT_ARITHMETIC_BLOCK_INPUT_BLOCKS_ID_EMPTY = "SingleInputArithmeticBlock : Input blocks Id empty";
    public static final String BLOCK_NAME_IS_NULL = "Block name is null";
    public static final String COMPARISON_BLOCK_UNABLE_TO_COMPARE_THRESHOLD_IS_NULL_AND_INPUT_BLOCKS_ID_CONTAINS_LESS_THAN_TWO_ELEMENTS = "ComparisonBlock : Unable to compare : Threshold is null and input blocks ID contains less than two elements";
    public static final String EXCEPTION_OCCURRED_WHILE_INITIALIZE = "Exception occurred while initialize : ";
    public static final String COMPARISON_BLOCK = "ComparisonBlock : ";
    public static final String ATTACHED_SETTER_BLOCK = "AttachedSetterBlock : ";
    public static final String ATTACHED_SETTER_BLOCK_EXCEPTION_OCCURRED = "AttachedSetterBlock : Exception occurred : ";
    public static final String EMAIL_BLOCK = "EmailBlock : ";
    public static final String LINKED_ATTACHED_SETTER_BLOCK_INPUT_BLOCKS_ID_SIZE_NOT_EQUAL_1 = "LinkedAttachedSetterBlock : Input blocks ID size not equal 1";
    public static final String LINKED_ATTACHED_SETTER_BLOCK = "LinkedAttachedSetterBlock : ";
    public static final String LINKED_SETTER_BLOCK_INPUT_BLOCKS_ID_EMPTY = "LinkedSetterBlock : Input blocks ID empty";
    public static final String LINKED_SETTER_BLOCK = "LinkedSetterBlock : ";
    public static final String LINKED_SETTER_BLOCK_CLASS_ID_IS_NULL = "LinkedSetterBlock : Class ID is null";
    public static final String LINKED_SETTER_BLOCK_CLASS_PATH_IS_NULL = "LinkedSetterBlock : Class Path is null";
    public static final String LINKED_SETTER_BLOCK_DESTINATION_ATTRIBUTE_NAME_IS_NULL = "LinkedSetterBlock : Destination Attribute Name is null";
    public static final String LINKED_SETTER_BLOCK_INSTANCES_LIST_NULL = "LinkedSetterBlock : Instances list null";
    public static final String LINKED_SETTER_BLOCK_INSTANCES_LIST_EMPTY = "LinkedSetterBlock : Instances list empty";
    public static final String LINKED_VARIABLE_SETTER_BLOCK_INPUT_BLOCKS_ID_EMPTY = "LinkedVariableSetterBlock : Input blocks ID empty";
    public static final String LINKED_VARIABLE_SETTER_BLOCK = "LinkedVariableSetterBlock : ";
    public static final String INFORM_BLOCK_LOG_MESSAGE_NULL = "InformBlock : log message null";
    public static final String INFORM_BLOCK_LOG_MESSAGE_EMPTY = "InformBlock : log message empty";
    public static final String SETTER_BLOCK = "SetterBlock : ";
    public static final String SET_VARIABLE_BLOCK = "SetVariableBlock : ";
    public static final String SET_VARIABLE_BLOCK_VARIABLE_ID_NULL = "SetVariableBlock : Variable Id null";
    public static final String SMSBLOCK_SERVER_TYPE_NULL = "SMSBlock : Server Type null";
    public static final String SMSBLOCK = "SMSBlock : ";
    public static final String TRIGGER_EVENT_BLOCK_EVENT_ID_NULL = "TriggerEventBlock : event Id null";
    public static final String INSTANCE_GETTER_BLOCK_EXCEPTION_WHILE_LOADING_CLASSES_INSTANCES_INFO = "InstanceGetterBlock : Exception while loading Classes / Instances info : ";
    public static final String INSTANCE_GETTER_BLOCK_CLASS_ID_IS_NULL = "InstanceGetterBlock : Class Id is null";
    public static final String INSTANCE_GETTER_BLOCK_CLASS_PATH_IS_NULL = "InstanceGetterBlock : Class Path is null";
    public static final String VARIABLE_GETTER_BLOCK_EXCEPTION_WHILE_LOADING_VARIABLE_ID = "VariableGetterBlock : Exception while loading variable Id : ";
    public static final String VARIABLE_GETTER_BLOCK_VARIABLE_ID_IS_NULL = "VariableGetterBlock : Variable Id is null";


}
