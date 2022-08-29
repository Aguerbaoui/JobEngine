package io.je.utilities.beans;

import io.je.utilities.models.EventType;
import io.je.utilities.monitoring.JEMonitor;
import io.je.utilities.monitoring.MonitoringMessage;
import io.je.utilities.monitoring.ObjectType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "JEEventCollection")
public class JEEvent extends JEMonitoredData {

    private String triggeredById;

    private EventType type;

    private String description;

    private boolean isTriggered = false;

    private long lastTriggerTime;


    private int timeoutValue;

    private String timeoutUnit;

    private int timeout; // milliseconds

    @SuppressWarnings("unused")
    private JEEvent() {

    }

    public JEEvent(String jobEngineElementID, String jobEngineProjectID, String name, EventType type,
                   String description, int timeoutValue, String timeoutUnit, ArchiveOption isArchived,
                   boolean isBroadcasted, String createdBy, String modifiedBy) {
        super(jobEngineElementID, jobEngineProjectID, name);
        this.type = type;
        this.triggeredById = jobEngineElementID;
        this.description = description;
        this.timeoutValue = timeoutValue;
        this.timeoutUnit = timeoutUnit;
        this.jeObjectCreatedBy = createdBy;
        this.jeObjectModifiedBy = modifiedBy;
        setTimeout();
    }

    public JEEvent(String jobEngineElementID, String jobEngineProjectID, String name, EventType type,
                   String description, int timeoutValue, String timeoutUnit, String createdBy, String modifiedBy) {
        super(jobEngineElementID, jobEngineProjectID, name);
        this.type = type;
        this.triggeredById = jobEngineElementID;
        this.description = description;
        this.timeoutValue = timeoutValue;
        this.timeoutUnit = timeoutUnit;
        this.jeObjectCreatedBy = createdBy;
        this.jeObjectModifiedBy = modifiedBy;
        setTimeout();
    }

    public String getTriggeredById() {
        return triggeredById;
    }

    public void setTriggeredById(String triggeredById) {
        this.triggeredById = triggeredById;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public long getLastTriggerTime() {
        return lastTriggerTime;
    }

    public void setLastTriggerTime(long lastTriggerTime) {
        this.lastTriggerTime = lastTriggerTime;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private void setTimeout() {
        if (timeoutValue != 0) {
            switch (timeoutUnit) {
                case "s":
                    timeout = timeoutValue * 1000;
                    break;
                case "m":
                    timeout = timeoutValue * 60 * 1000;
                    break;
                case "h":
                    timeout = timeoutValue * 3600 * 1000;
                    break;
                default:
                    timeout = timeoutValue * 1000;
                    break;
            }
        }

    }

    public boolean isTriggered() {
        return isTriggered;
    }

    public void setTriggered(boolean isTriggered) {
        this.isTriggered = isTriggered;
    }

    public void trigger() {
        isTriggered = true;
        lastTriggerTime = System.nanoTime();
        JEMonitor.publish(MonitoringMessage.getMonitoringMessage(LocalDateTime.now(), jobEngineElementID, ObjectType.JEEVENT, jobEngineProjectID, String.valueOf(isTriggered), Status.TRIGGERED));


    }

    public void untrigger() {
        isTriggered = false;
        lastTriggerTime = System.nanoTime();
        JEMonitor.publish(MonitoringMessage.getMonitoringMessage(LocalDateTime.now(), jobEngineElementID, ObjectType.JEEVENT, jobEngineProjectID, String.valueOf(isTriggered), Status.NOT_TRIGGERED));


    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTimeoutValue() {
        return timeoutValue;
    }

    public void setTimeoutValue(int timeoutValue) {
        this.timeoutValue = timeoutValue;
        setTimeout();

    }

    public String getTimeoutUnit() {
        return timeoutUnit;
    }

    public void setTimeoutUnit(String timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
    }

}
