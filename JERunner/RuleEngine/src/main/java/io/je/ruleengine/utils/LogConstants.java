package io.je.ruleengine.utils;

public class LogConstants {
	
	private LogConstants()
	{
		
	}

	//rules
	
	//building project
	public static String buildingProjectContainer = "Building Project Container...";
	public static String buildingProjectContainerSuccessful = "Project Container built Succesfully";
	public static String buildingProjectContainerFailed = "Project Container built failed";
	
	//running project
	public static String failedToUpdateContainer = "Failed to update kie container";
	public static String projectAlreadyRunning  = "This project container is already running";
	public static String stoppingProjectContainer = "Stopping project container execution";
	public static String stoppingProjectContainerSuccessful = "Project stopped.";
	public static String stoppingProjectContainerFailed = "Failed to stop project";
	
	public static String unexpectedError = "";
	public static String sucessfullyAddedRule = "";
	public static String failedToAddRule = "";
	public static String sucessfullyUpdatedRule = "";
	public static String failedToUpdateRule = "";
	public static String sucessfullyDeletedRule = "";
	public static String failedToDeleteRule = "";
	public static String sucessfullyCompiledRule="";
	public static String ruleCompilationError="";
	public static String ruleExists = "A rule with this id already exists.";
	public static String failedToFireRules = "";
}
