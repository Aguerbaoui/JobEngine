package io.je.utilities.logger;

import com.fasterxml.jackson.annotation.JsonProperty;


public class LogMessage {
	
    @JsonProperty("LogLevel")
	public LogLevel logLevel ;
    
    @JsonProperty("Message")
    public Object message ;
    
    @JsonProperty("LogDate")
    public String logDate ;
    
    @JsonProperty("Module")
    public String module="JobEngine" ;
    
    @JsonProperty("Category")
    public LogCategory category ;
    
    @JsonProperty("ProjecId")
    public String projectId ;
    
    
    //TODO change type to LogSubModules
    @JsonProperty("SubModule")
    public String subModule ;
	
    
    public LogMessage(LogLevel logLevel, Object message, String logDate, LogCategory category,
			String projectId, LogSubModules subModule) {
		super();
		this.logLevel = logLevel;
		this.message = message;
		this.logDate = logDate;
		this.category = category;
		this.projectId = projectId;
		this.subModule = subModule.toString();
	}

    
    /*
     * temporary until we add object id
     */
    public LogMessage(LogLevel logLevel, Object message, String logDate, LogCategory category,
			String projectId, String subModule) {
		super();
		this.logLevel = logLevel;
		this.message = message;
		this.logDate = logDate;
		this.category = category;
		this.projectId = projectId;
		this.subModule = subModule;
	}
    
    
}
