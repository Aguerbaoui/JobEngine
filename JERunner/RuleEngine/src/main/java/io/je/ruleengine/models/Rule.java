package io.je.ruleengine.models;

import java.util.ArrayList;
import java.util.Set;

import io.je.ruleengine.enumerations.RuleFormat;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.runtimeobject.JEObject;

/*
 *Rule Definition
 */
public class Rule extends JEObject {


    //Rule Name
    String name;
    //Rule Type ( drl, csv ...)
    RuleFormat resourceType;
    //Rule file path
    String path;
    //Rule file content
    String content;

    ArrayList<JEEvent> events;

    public Rule(String jobEngineElementID, String jobEngineProjectID, String name, RuleFormat resourceType,
                String path) {
        super(jobEngineElementID, jobEngineProjectID);
        this.name = name;
        this.resourceType = resourceType;
        this.path = path;

    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public RuleFormat getResourceType() {
        return resourceType;
    }


    public void setResourceType(RuleFormat resourceType) {
        this.resourceType = resourceType;
    }


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }




    @Override
	public String toString() {
		return "Rule [path=" + path + ", content=" + content + "]";
	}

	public ArrayList<JEEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<JEEvent> events) {
        this.events = events;
    }

    public void addEvent(JEEvent e) {
        if(events == null) {
            events = new ArrayList<>();
        }
        events.add(e);
    }
}

