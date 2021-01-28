package io.je;

public class JEProcess {

    private String key;

    private String name;

    private String bpmnPath;

    private boolean triggeredByEvent;

    private boolean running = false;

    private boolean deployed = false;

    private String projectId;

    public JEProcess(String key, String name, String bpmnPath, String projectId, boolean triggeredByEvent) {
        super();
        this.key = key;
        this.name = name;
        this.bpmnPath = bpmnPath;
        this.projectId = projectId;
        this.triggeredByEvent = triggeredByEvent;
    }
    public boolean isTriggeredByEvent() {
        return triggeredByEvent;
    }

    public void setTriggeredByEvent(boolean triggeredByEvent) {
        this.triggeredByEvent = triggeredByEvent;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBpmnPath() {
        return bpmnPath;
    }

    public void setBpmnPath(String bpmnPath) {
        this.bpmnPath = bpmnPath;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }
}
