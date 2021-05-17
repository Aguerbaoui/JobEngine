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
    
    @JsonProperty("ObjectId")
    public String objectId;
    
    
    @JsonProperty("SubModule")
    public LogSubModule subModule ;
	
    
    public LogMessage(LogLevel logLevel, Object message, String logDate, LogCategory category,
			String projectId, LogSubModule subModule, String objectId) {
		super();
		this.logLevel = logLevel;
		this.message = message;
		this.logDate = logDate;
		this.category = category;
		this.projectId = projectId;
		this.subModule = subModule;
		this.objectId = objectId;
	}

    
   
    
    
}
