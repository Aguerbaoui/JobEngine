package io.je.processes;

import io.je.JEProcess;
import io.je.callbacks.OnExecuteOperation;
import io.je.serviceTasks.ActivitiTask;
import io.je.serviceTasks.InformTask;
import io.je.utilities.beans.JEMessages;
import io.je.utilities.exceptions.WorkflowAlreadyRunningException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.exceptions.WorkflwTriggeredByEventException;
import io.je.utilities.files.JEFileUtils;
import io.je.utilities.logger.JELogger;
import org.activiti.engine.*;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.*;

import static io.je.utilities.constants.ResponseCodes.WORKFLOW_EVENT_TRIGGER;


public class ProcessManager {


    /*
     * Activiti Workflow engine
     * */
    private ProcessEngine processEngine;

    /*
     * Runtime service for Activiti
     * */
    private RuntimeService runtimeService;

    /*
     * Task service for Activiti
     * */
    private TaskService taskService;


    /*
     * Management service for Activiti
     * */
    private ManagementService managementService;

    /*
     * Dynamic service for Activiti
     * */
    private DynamicBpmnService dyService;

    private HistoryService historyService;

    /*
     * Repository service for activiti
     * */
    private RepositoryService repoService;


    /*
     * List of all active processes
     * */
    private static HashMap<String, JEProcess> processes = new HashMap<>();


    /*
     * List of all possible workflow task executions
     * */
    private HashMap<String, OnExecuteOperation> allCallbacks = new HashMap<String, OnExecuteOperation>();


    /*
     * Initialize the workflow engine
     * */
    public ProcessManager() {

        processEngine = ProcessEngines.getDefaultProcessEngine();
        repoService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        //taskService.createTaskQuery().taskId(id); not the same as execution.id this has to be the original task id from the bpmn so we can map them
    }

    /*
     * Add a process to engine
     * */
    public void addProcess(JEProcess process) {

        if (processes == null) {
            processes = new HashMap<String, JEProcess>();
        }

        processes.put(process.getKey(), process);
    }

    /*
     * Register workflow execution callback
     * */
    public void registerWorkflowCallback(String id, OnExecuteOperation callback) {
        allCallbacks.put(id, callback);
    }

    /*
     * Complete a user task
     * */
    public void completeTask(Task task) {

        taskService.complete(task.getId());
    }

    /*
     * Deploy a process to engine
     * */
    public void deployProcess(String key) {
        ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
        //repoService.
        JELogger.trace(ProcessManager.class, JEMessages.DEPLOYING_IN_RUNNER_WORKFLOW_WITH_ID + " = " + key);
        String processXml = JEFileUtils.getStringFromFile(processes.get(key).getBpmnPath());
        DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment().name(key);
        deploymentBuilder.addString(key + ".bpmn", processXml);
        Deployment dep = deploymentBuilder.deploy();
        processes.get(key).setDeployed(true);
        JELogger.debug("Deleting bpmn file after loading ...");
        JEFileUtils.deleteFileFromPath(processes.get(key).getBpmnPath());
    }

    /*
     * Launch process by key without variables
     * */
    public void launchProcessByKeyWithoutVariables(String id) throws WorkflowNotFoundException, WorkflowAlreadyRunningException, WorkflwTriggeredByEventException {
        if (processes.get(id) == null) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        if (processes.get(id).getActivitiTasks().size() > 0) {
            launchProcessByKeyWithVariables(id);
        } else {
            if (processes.get(id).isRunning()) {
                throw new WorkflowAlreadyRunningException(JEMessages.WORKFLOW_ALREADY_RUNNING);
            }
            if (!processes.get(id).isTriggeredByEvent()) {
                processes.get(id).setRunning(true);
                if(processes.get(id).getProcessInstance() != null) {
                    removeProcess(id);
                }
                ProcessInstance p = runtimeService.startProcessInstanceByKey(id);
                processes.get(id).setProcessInstance(p);
                new Thread(() -> {
                    while(p != null && !p.isEnded()) {
                    }
                    processes.get(id).setRunning(false);
                }).start();
            } else {
                JELogger.error(ProcessManager.class, " " + JEMessages.PROCESS_HAS_TO_BE_TRIGGERED_BY_EVENT);
                throw new WorkflwTriggeredByEventException(WORKFLOW_EVENT_TRIGGER,JEMessages.PROCESS_HAS_TO_BE_TRIGGERED_BY_EVENT);
            }
        }

    }

    /*
     * Launch process by key wit variables
     * */
    public void launchProcessByKeyWithVariables(String id) throws WorkflowAlreadyRunningException {
        if(processes.get(id).isRunning()) {
            throw new WorkflowAlreadyRunningException(JEMessages.WORKFLOW_ALREADY_RUNNING);
        }
        if (!processes.get(id).isTriggeredByEvent()) {
            Map<String, Object> variables = new HashMap<>();
            for (ActivitiTask task : processes.get(id).getActivitiTasks().values()) {
                if (task instanceof InformTask) {
                    variables.put(task.getTaskId(), ((InformTask) task).getMessage());
                }
            }
            processes.get(id).setRunning(true);
            try {
                if(processes.get(id).getProcessInstance() != null) {
                    removeProcess(id);
                }
                ProcessInstance p = runtimeService.startProcessInstanceByKey(id, variables);
                processes.get(id).setProcessInstance(p);
                new Thread(() -> {
                    while(p != null && !p.isEnded()) {
                    }
                    processes.get(id).setRunning(false);
                }).start();
            }
            catch(BpmnError e) {
                JELogger.error("Error = " + Arrays.toString(e.getStackTrace()));
            }
        }
        else {
            JELogger.error(ProcessManager.class, " " + JEMessages.PROCESS_HAS_TO_BE_TRIGGERED_BY_EVENT);
        }


    }

    /*
     * Launch process by message event without variables
     * */
    public void launchProcessByMessageWithoutVariables(String messageId) {

        try {
            for(JEProcess process: processes.values()) {
                if(process.isTriggeredByEvent() && process.getTriggerMessage().equals(messageId)) {
                    Map<String, Object> variables = new HashMap<>();
                    for (ActivitiTask task : process.getActivitiTasks().values()) {
                        if (task instanceof InformTask) {
                            variables.put(task.getTaskId(), ((InformTask) task).getMessage());
                        }
                    }
                    ProcessInstance p = runtimeService.startProcessInstanceByMessage(messageId, variables);
                    new Thread(() -> {
                        while(p != null && !p.isEnded()) {
                        }
                        process.setRunning(false);
                    }).start();
                    process.setRunning(true);
                    break;
                }
            }
            //runtimeService.startProcessInstanceByMessage(messageId);
        } catch (ActivitiObjectNotFoundException e) {
            JELogger.error(ProcessManager.class, Arrays.toString(e.getStackTrace()));
        }
        catch(Exception e) {
            JELogger.error("Error in launching a process by message " + Arrays.toString(e.getStackTrace()));
        }
    }

    /*
     * Throw signal in engine
     * */
    public void throwSignal(String signalId) {
       /* String executionId = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName(signalId).singleResult().getId();*/
        runtimeService.signalEventReceived(signalId);
    }

    /*
     * Throw signal in workflow
     * */
    /*public void throwSignalInProcess(String signalId, String executionId) {

        runtimeService.signalEventReceived(signalId, executionId);
    }
*/
    /*
     * Returns a list of all signal event subscriptions
     * */
    public List<Execution> getAllSignalEventSubscriptions(String signalId) {

        return runtimeService.createExecutionQuery()
                .signalEventSubscriptionName(signalId)
                .list();
    }

    /*
     * Throw a message event in workflow
     * */
    public void throwMessageEvent(String messageId, String executionId) {

        runtimeService.messageEventReceived(messageId, executionId);
    }

    /*
     * Throw a message event
     * */
    public void throwMessageEvent(String messageId) {

        String executionId = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName(messageId).singleResult().getId();
        if (executionId != null) {
            throwMessageEvent(messageId, executionId);
        } else {
            launchProcessByMessageWithoutVariables(messageId);
        }
    }

    /*
     * Returns the process execution subscribed to message event
     * */
    public Execution getMessageEventSubscription(String messageId) {

        return runtimeService.createExecutionQuery()
                .messageEventSubscriptionName(messageId).singleResult();

    }


    /*
     * Get workflow Engine
     * */
    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    /*
     * Get runtime service
     * */
    public RuntimeService getRuntimeService() {
        return runtimeService;
    }

    /*
     * Get the engine task service
     * */
    public TaskService getTaskService() {
        return taskService;
    }

    /*
     *Get the engine management service
     * */
    public ManagementService getManagementService() {
        return managementService;
    }

    /*
     * Get the engine dynamic service
     * */
    public DynamicBpmnService getDyService() {
        return dyService;
    }

    /*
     * Get the engine repository service
     * */
    public RepositoryService getRepoService() {
        return repoService;
    }

    /*
     * Get all deployed processes
     * */
    public HashMap<String, JEProcess> getProcesses() {
        return processes;
    }


    public void runAll(String projectId) throws WorkflowNotFoundException {
        JELogger.trace(" " + JEMessages.RUNNING_ALL_WORKFLOWS_IN_PROJECT_ID + " = " + projectId);
        for (JEProcess process : processes.values()) {
            if (process.getProjectId().equals(projectId) && process.isDeployed() && !process.isRunning()) {
                try {
                    launchProcessByKeyWithoutVariables(process.getKey());
                } catch (WorkflowAlreadyRunningException e) {
                    JELogger.error(ProcessManager.class, JEMessages.WORKFLOW_ALREADY_RUNNING + process.getKey());
                } catch (WorkflwTriggeredByEventException e) {
                    JELogger.error(ProcessManager.class, JEMessages.PROCESS_HAS_TO_BE_TRIGGERED_BY_EVENT + process.getKey());
                }
            }
        }
    }

    public void buildProjectWorkflows(String projectId) {
        JELogger.info(JEMessages.BUILDING_WORKFLOWS_IN_PROJECT + " id = " + projectId);
        for (JEProcess process : processes.values()) {
            if (process.getProjectId().equals(projectId) && !process.isDeployed()) {
                deployProcess(process.getKey());
            }
        }
    }

    public void stopProcess(String key) {
        if (processes.get(key).isRunning()) {
            try {
                runtimeService.deleteProcessInstance(processes.get(key).getProcessInstance().getProcessInstanceId(), "User Stopped the execution");
                processes.get(key).setRunning(false);
            } catch (ActivitiObjectNotFoundException e) {
                JELogger.trace(ProcessManager.class, " " + JEMessages.ERROR_DELETING_A_NON_EXISTING_PROCESS);
            }
        }
    }

    public void stopProjectWorkflows() {

        for (JEProcess process : processes.values()) {
            if (process.isRunning()) {
                try {
                    runtimeService.deleteProcessInstance(process.getProcessInstance().getProcessInstanceId(), "User Stopped the execution");
                    process.setRunning(false);
                } catch (ActivitiObjectNotFoundException e) {
                    JELogger.trace(ProcessManager.class, " " + JEMessages.ERROR_DELETING_A_NON_EXISTING_PROCESS);
                }
            }
        }
    }

    public static void setRunning(String id, boolean b, String processInstanceId) {
       /* String key = id.substring(id.indexOf(':'), id.length());
        processes.get(id.replace(key, "")).setRunning(b);
        if(processes.get(id.replace(key, "")).isRunning()) {

        }
        processes.get(id.replace(key, "")).setActivitiKey(processInstanceId);*/
    }


    //Stopr/remove workflow
    public void removeProcess(String workflowId) {
        try {

            JEProcess p = processes.get(workflowId);
            runtimeService.deleteProcessInstance(p.getProcessInstance().getProcessInstanceId(), "User Deleted the process");

        } catch (ActivitiObjectNotFoundException e) {
            JELogger.trace(" " + JEMessages.ERROR_DELETING_A_NON_EXISTING_PROCESS);
        } catch (Exception e) {
            JELogger.trace(" " + JEMessages.ERROR_DELETING_A_PROCESS + "\n" + Arrays.toString(e.getStackTrace()));
        }

    }
}
