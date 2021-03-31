package io.je.utilities.constants;

public class RuleEngineErrors {
	
    public static final String ID_NOT_FOUND = "Failed to read rule id";
    public static final String RULE_EXISTS ="Rule already exists";
    public static final String RULE_CONTAINS_ERRORS ="Rule contains errors";
    public static final String RULE_FILE_NOT_FOUND ="Rule file was not found";
    public static final String RULE_PROJECT_ID_NOT_FOUND ="Rule project id was not found";

    //building project
    public static String buildingProjectContainer = "Building Project Container...";

    //rules
    public static String buildingProjectContainerSuccessful = "Project Container built Succesfully";
    public static String buildingProjectContainerFailed = "Project Container build Failed";
    //running project
    public static String failedToUpdateContainer = "Failed to update kie container";
    public static String projectAlreadyRunning = "This project container is already running";
    public static String stoppingProjectContainer = "Stopping project container execution";
    public static String stoppingProjectContainerSuccessful = "Project stopped.";
    public static String stoppingProjectContainerFailed = "Failed to stop project container";
    public static String unexpectedError = "An unexpected error occured";
    public static String successfullyAddedRule = "Rule successfully added";
    public static String failedToAddRule = "Failed to add rule";
    public static String sucessfullyUpdatedRule = "Successfully updated rule";
    public static String failedToUpdateRule = "Failed to update rule";
    public static String sucessfullyDeletedRule = "rule deleted from engine";
    public static String failedToDeleteRule = "Failed to delete rule";
    public static String sucessfullyCompiledRule = "rule compiled successfully";
    public static String ruleCompilationError = "Failed to compile Rule";
    public static String ruleExists = "A rule with this id already exists.";
    public static String failedToFireRules = "Failed to fire rules";

}
