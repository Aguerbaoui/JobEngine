package io.je.utilities.beans;

import io.je.utilities.runtimeobject.JEObject;

import java.sql.Timestamp;

public class JEEvent extends JEObject {

    public static final String START_WORKFLOW = "START_WORKFLOW";

    public static final String START_RULE = "START_RULE";

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";

    public static final String SIGNAL_EVENT = "SIGNAL_EVENT";

    private String name;

    private String triggeredById;

    private String type;

    public EventTriggeredCallback getTriggeredCallback() {
        return triggeredCallback;
    }

    public void setTriggeredCallback(EventTriggeredCallback triggeredCallback) {
        this.triggeredCallback = triggeredCallback;
    }

    private EventTriggeredCallback triggeredCallback;

    /*
    * The reference in case of a rule should be the rule id, in case of a workflow its the message or signal reference
    * */
    private String reference;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTriggeredById() {
        return triggeredById;
    }

    public void setTriggeredById(String triggeredById) {
        this.triggeredById = triggeredById;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JEEvent() {}

    public JEEvent(String jobEngineElementID, String jobEngineProjectID, String name, String type) {
        super(jobEngineElementID, jobEngineProjectID);
        this.name = name;
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
