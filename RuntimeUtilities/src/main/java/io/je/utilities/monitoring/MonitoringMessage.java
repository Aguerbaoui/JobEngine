package io.je.utilities.monitoring;

import io.je.utilities.beans.Status;

import java.time.LocalDateTime;

public class MonitoringMessage {

    String timestamp;
    String objectId;
    ObjectType objectType; //Variable/Event : ClassName
    String objectProjectId;
    String objectValue;
    String status;
	/*ArchiveOption isArchived = ArchiveOption.AS_SOURCE_DATA;
	boolean isBroadcasted=true;
	String Source="JobEngine";*/


    public MonitoringMessage() {
    }

    public MonitoringMessage(LocalDateTime timestamp, String objectId, ObjectType objectType, String objectProjectId,
                             String objectValue, String status) {
        super();
        this.timestamp = timestamp.toString();
        this.objectId = objectId;
        this.objectType = objectType;
        this.objectProjectId = objectProjectId;
        this.objectValue = objectValue;
        this.status = status;
		/*this.isArchived = isArchived;
		this.isBroadcasted = isBroadcasted;*/
    }

    public static MonitoringMessage getMonitoringMessage(LocalDateTime timestamp, String objectId, ObjectType objectType, String objectProjectId,
                                                         String objectValue, Status status) {
        return new MonitoringMessage(timestamp, objectId, objectType, objectProjectId, objectValue, status.toString());
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp.toString();
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
	/*public ArchiveOption getIsArchived() {
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
	}*/

    public String getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(String objectValue) {
        this.objectValue = objectValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MonitoringMessage{" +
                "timestamp='" + timestamp + '\'' +
                ", objectId='" + objectId + '\'' +
                ", objectType=" + objectType +
                ", objectProjectId='" + objectProjectId + '\'' +
                ", objectValue='" + objectValue + '\'' +
                ", status=" + status +
                '}';
    }
}


