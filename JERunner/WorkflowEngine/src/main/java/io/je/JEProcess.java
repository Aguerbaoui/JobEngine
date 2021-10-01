package io.je;

import io.je.serviceTasks.ActivitiTask;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.HashMap;

public class JEProcess {

    private String key;

    private String name;

    private String bpmnPath;

    private boolean triggeredByEvent;

    private String triggerMessage;

    private boolean running = false;

    private boolean deployed = false;

    private String projectId;

    private ProcessInstance processInstance;

    /*
     * True if the workflow starts with project boot
     * */
    private boolean onProjectBoot = false;

    HashMap<String, ActivitiTask> activitiTasks;

    public JEProcess() {
        activitiTasks = new HashMap<>();
    }

    public JEProcess(String key, String name, String bpmnPath, String projectId, boolean triggeredByEvent) {
        super();
        this.key = key;
        this.name = name;
        this.bpmnPath = bpmnPath;
        this.projectId = projectId;
        this.triggeredByEvent = triggeredByEvent;
        activitiTasks = new HashMap<>();

    }

    public String getTriggerMessage() {
        return triggerMessage;
    }

    public void setTriggerMessage(String triggerMessage) {
        this.triggerMessage = triggerMessage;
    }

    public boolean isTriggeredByEvent() {
        return triggeredByEvent;
    }

    public HashMap<String, ActivitiTask> getActivitiTasks() {
        return activitiTasks;
    }

    public void setActivitiTasks(HashMap<String, ActivitiTask> activitiTasks) {
        this.activitiTasks = activitiTasks;
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

    public void addActivitiTask(ActivitiTask activitiTask) {
        this.activitiTasks.put(activitiTask.getTaskId(), activitiTask);
    }

    public boolean isOnProjectBoot() {
        return onProjectBoot;
    }

    public void setOnProjectBoot(boolean onProjectBoot) {
        this.onProjectBoot = onProjectBoot;
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }
}
