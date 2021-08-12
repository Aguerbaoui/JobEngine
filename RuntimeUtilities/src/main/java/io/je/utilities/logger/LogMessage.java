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
    
   /* @JsonProperty("Category")
    public LogCategory category ;*/
    
    @JsonProperty("ProjectId")
    public String projectId ;
    
    @JsonProperty("ObjectId")
    public String objectId;
    
    
    @JsonProperty("SubModule")
    public LogSubModule subModule ;
    
    @JsonProperty("Source")
    public String source ;
    
   /* @JsonProperty("Value")
    public Object value ;*/

    @JsonProperty("Type")
    public String type = "Log";
    
   /* @JsonProperty("Description")
    public String description ="N/A" ;*/


	 public LogMessage(LogLevel logLevel, Object message, String logDate,
				String projectId, LogSubModule subModule, String objectId) {
			super();
			this.logLevel = logLevel;
			this.message = message;
			this.logDate = logDate;
			this.projectId = projectId;
			this.subModule = subModule;
			this.objectId = objectId;
		}


	private LogMessage() {
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public String getLogDate() {
		return logDate;
	}

	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	/*public LogCategory getCategory() {
		return category;
	}

	public void setCategory(LogCategory category) {
		this.category = category;
	}*/

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public LogSubModule getSubModule() {
		return subModule;
	}

	public void setSubModule(LogSubModule subModule) {
		this.subModule = subModule;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/*public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}*/

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


/*	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}*/
	
    



}
