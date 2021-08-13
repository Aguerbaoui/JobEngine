package io.je.utilities.monitoring;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.utilities.beans.ArchiveOption;

public class MonitoringMessage {
	
	String timestamp;
	String objectId;
	@JsonProperty("ModelId")
	ObjectType objectType; //Variable/Event : ClassName
	String objectProjectId;
	String objectValue;
	ArchiveOption isArchived = ArchiveOption.asInstance;
	boolean isBroadcasted=true;
	
	
	
	
	
	public MonitoringMessage(LocalDateTime timestamp, String objectId, ObjectType objectType, String objectProjectId,
			String objectValue, ArchiveOption isArchived, boolean isBroadcasted) {
		super();
		this.timestamp = timestamp.toString();
		this.objectId = objectId;
		this.objectType = objectType;
		this.objectProjectId = objectProjectId;
		this.objectValue = objectValue;
		this.isArchived = isArchived;
		this.isBroadcasted = isBroadcasted;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp.toString();
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public ObjectType getObjectType() {
		return objectType;
	}
	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}
	public String getObjectProjectId() {
		return objectProjectId;
	}
	public void setObjectProjectId(String objectProjectId) {
		this.objectProjectId = objectProjectId;
	}
	public String getObjectValue() {
		return objectValue;
	}
	public void setObjectValue(String objectValue) {
		this.objectValue = objectValue;
	}
	public ArchiveOption getIsArchived() {
		return isArchived;
	}
	public void setIsArchived(ArchiveOption isArchived) {
		this.isArchived = isArchived;
	}
	public boolean getIsBroadcasted() {
		return isBroadcasted;
	}
    
	public void setisBroadcasted(boolean isBroadcasted) {
		this.isBroadcasted = isBroadcasted;
	}
	
	
	
	
	
	
	
	
	

}


