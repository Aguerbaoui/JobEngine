package io.je.ruleengine.models;

import io.je.ruleengine.enumerations.RuleFormat;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.runtimeobject.JEObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
 *Rule Definition
 */
public class Rule extends JEObject {

    //Rule Type ( drl, csv ...)
    RuleFormat resourceType;
    //Rule file path
    String path;
    //Rule file content
    String content;
    //topics rule is subscribed to
    Set<String> topics = new HashSet<>();

    ArrayList<JEEvent> events;

    public Rule(String jobEngineElementID, String jobEngineProjectID, String jobEngineElementName, String jobEngineProjectName,
                RuleFormat resourceType, String path) {
        super(jobEngineElementID, jobEngineProjectID, jobEngineElementName, jobEngineProjectName);
        this.resourceType = resourceType;
        this.path = path;

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
        if (events == null) {
            events = new ArrayList<>();
        }
        events.add(e);
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }


}

