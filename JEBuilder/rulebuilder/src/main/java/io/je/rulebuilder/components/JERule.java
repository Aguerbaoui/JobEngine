package io.je.rulebuilder.components;

import io.je.utilities.beans.Status;
import io.je.utilities.monitoring.JEMonitor;
import io.je.utilities.monitoring.MonitoringMessage;
import io.je.utilities.monitoring.ObjectType;
import io.je.utilities.runtimeobject.JEObject;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Document(collection = "JERuleCollection")
public abstract class JERule extends JEObject {


    // FIXME why not used
    protected Map<String, Integer> classTopics = new HashMap<>();
    protected Map<String, Integer> instanceTopics = new HashMap<>();
    /*
     * buildStatus
     */
    boolean isBuilt = false;
    boolean isCompiled = false;
    /*
     * check if rule was added to JERunner or not
     */
    // FIXME is it really used
    boolean isAdded = false;
    boolean isRunning = false;
    boolean enabled = true;
    @Transient
    boolean containsErrors = false;
    Status status = Status.NOT_BUILT;
    String description;
    String ruleFrontConfig;
    @Transient
    private Map<String, Integer> topics = new HashMap<>();


    public JERule(String jobEngineElementID, String jobEngineProjectID, String ruleName) {
        super(jobEngineElementID, jobEngineProjectID, ruleName);
    }

    public JERule(String jobEngineElementID, String jobEngineProjectID, String ruleName, String projectName) {
        super(jobEngineElementID, jobEngineProjectID, ruleName, projectName);
    }


    public JERule() {
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public void setBuilt(boolean isBuilt) {
        if (!isBuilt) {
            this.isCompiled = false;
        }
        this.isBuilt = isBuilt;

    }

    public boolean isAdded() {
        return isAdded;
    }

    // FIXME not used
    public void setAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }

    public boolean isCompiled() {
        return isCompiled;
    }

    public void setCompiled(boolean isCompiled) {
        this.isCompiled = isCompiled;
    }

    public String getRuleFrontConfig() {
        return ruleFrontConfig;
    }

    public void setRuleFrontConfig(String ruleFrontConfig) {
        this.ruleFrontConfig = ruleFrontConfig;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addTopic(String topic, Map<String, Integer> topics) {
        if (topic != null) {
            if (!topics.containsKey(topic)) {
                topics.put(topic, 1);
            } else {
                topics.put(topic, topics.get(topic) + 1);
            }
        }

    }


    public void updateTopic(String oldTopic, String newTopic, Map<String, Integer> topics) {
        removeTopic(oldTopic, topics);
        addTopic(newTopic, topics);
    }

    public void removeTopic(String topic, Map<String, Integer> topics) {
        if (topics.containsKey(topic)) {
            topics.put(topic, topics.get(topic) - 1);
            if (topics.get(topic) == 0) {
                topics.remove(topic);
            }
        }
    }

    public Map<String, Integer> getTopics() {
        return topics;
    }

    public void setTopics(Map<String, Integer> topics) {
        this.topics = topics;
    }

    public void resetAllTopics() {
        this.topics = new ConcurrentHashMap();
        this.classTopics = new ConcurrentHashMap();
        this.instanceTopics = new ConcurrentHashMap();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;

        MonitoringMessage msg = new MonitoringMessage(LocalDateTime.now(), getJobEngineElementName(), ObjectType.JERULE,
                getJobEngineProjectName(), status.toString(), status.toString());

        JEMonitor.publish(msg);
    }

    public void loadTopics() {
        // TODO Auto-generated method stub

    }

    public boolean containsErrors() {
        return containsErrors;
    }

    public void setContainsErrors(boolean containsErrors) {
        this.containsErrors = containsErrors;
    }


}
