package io.je.processes;

import io.je.JEProcess;
import io.je.callbacks.OnExecuteOperation;
import io.je.serviceTasks.ActivitiTask;
import io.je.serviceTasks.InformTask;
import io.je.utilities.beans.Status;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;

import io.je.utilities.monitoring.JEMonitor;
import io.je.utilities.monitoring.MonitoringMessage;
import io.je.utilities.monitoring.ObjectType;
import org.activiti.engine.*;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.time.LocalDateTime;
import java.util.*;

import static io.je.utilities.constants.JEMessages.SENDING_WORKFLOW_MONITORING_DATA_TO_JEMONITOR;
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
    public void deployProcess(String key) throws WorkflowBuildException {
        ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
        MonitoringMessage msg = new MonitoringMessage(LocalDateTime.now(), key, ObjectType.JEWORKFLOW,
                processes.get(key).getProjectId(), String.valueOf(Status.BUILDING), Status.BUILDING);
        JELogger.debug(SENDING_WORKFLOW_MONITORING_DATA_TO_JEMONITOR + "\n" + msg, LogCategory.RUNTIME, "", LogSubModule.WORKFLOW, processes.get(key).getName());
        JEMonitor.publish(msg);
        //repoService.
        try {
            JELogger.debug(JEMessages.DEPLOYING_IN_RUNNER_WORKFLOW_WITH_ID + " = " + key,
                    LogCategory.RUNTIME, processes.get(key).getProjectId(),
                    LogSubModule.WORKFLOW, key);
            String processXml = FileUtilities.getStringFromFile(processes.get(key).getBpmnPath());
            DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment().name(key);
            deploymentBuilder.addString(key + ".bpmn", processXml);
            Deployment dep = deploymentBuilder.deploy(); //to debug it if needed
            //String id = deploymentBuilder.deploy().getId();
            processes.get(key).setDeployed(true);
            FileUtilities.deleteFileFromPath(processes.get(key).getBpmnPath());
            msg = new MonitoringMessage(LocalDateTime.now(), key, ObjectType.JEWORKFLOW,
                    processes.get(key).getProjectId(), String.valueOf(Status.STOPPED), Status.STOPPED);
            JELogger.debug(SENDING_WORKFLOW_MONITORING_DATA_TO_JEMONITOR + "\n" + msg, LogCategory.RUNTIME, "", LogSubModule.WORKFLOW, processes.get(key).getName());
            JEMonitor.publish(msg);
        } catch (Exception e) {
            throw new WorkflowBuildException(JEMessages.WORKFLOW_BUILD_ERROR + " with id = " + key);
        }
    }

    /*
     * Launch process by key without variables
     * */
    public void launchProcessByKeyWithoutVariables(String id, boolean runProject) throws WorkflowNotFoundException, WorkflowAlreadyRunningException, WorkflowBuildException, WorkflowRunException {
        JEProcess process = processes.get(id);
        if (process == null) {
            throw new WorkflowNotFoundException(JEMessages.WORKFLOW_NOT_FOUND);
        }
        if (process.getActivitiTasks().size() > 0) {
            launchProcessByKeyWithVariables(id, runProject);
        } else {
            if (process.isRunning()) {
                throw new WorkflowAlreadyRunningException(JEMessages.WORKFLOW_ALREADY_RUNNING);
            }
            //TODO add support for scheduled workflows
            if (!process.isTriggeredByEvent() && (process.isOnProjectBoot() || !runProject)) {

                process.setActiveThread(new Thread(() -> {
                    ProcessInstance p = runtimeService.startProcessInstanceByKey(id);
                    process.setRunning(true);
                }));
                process.getActiveThread().start();
            } else {
                JELogger.error(JEMessages.PROCESS_HAS_TO_BE_TRIGGERED_BY_EVENT,
                        LogCategory.RUNTIME, processes.get(id).getProjectId(),
                        LogSubModule.WORKFLOW, id);
                //TODO blocking exception to be removed forget not
                throw new WorkflowRunException(JEMessages.PROCESS_HAS_TO_BE_TRIGGERED_BY_EVENT);
            }
        }

    }

    /*
     * Launch process by key wit variables
     * */
    public void launchProcessByKeyWithVariables(String id, boolean runProject) throws WorkflowAlreadyRunningException, WorkflowBuildException {
        JEProcess process = processes.get(id);
        if (process.isRunning()) {
            throw new WorkflowAlreadyRunningException(JEMessages.WORKFLOW_ALREADY_RUNNING);
        }
        if (!process.isTriggeredByEvent() && (process.isOnProjectBoot() || !runProject)) {
            Map<String, Object> variables = new HashMap<>();
            for (ActivitiTask task : processes.get(id).getActivitiTasks().values()) {
                if (task instanceof InformTask) {
                    variables.put(task.getTaskId(), ((InformTask) task).getMessage());
                }
            }

            try {
                process.setActiveThread(new Thread(() -> {
                    ProcessInstance p = runtimeService.startProcessInstanceByKey(id, variables);

                    process.setRunning(true);
                }));
                process.getActiveThread().start();
            } catch (BpmnError e) {
                JELogger.error("Error to be removed after dev = " + Arrays.toString(e.getStackTrace()),
                        LogCategory.RUNTIME, processes.get(id).getProjectId(),
                        LogSubModule.WORKFLOW, id);
                throw new WorkflowBuildException(JEMessages.WORKFLOW_RUN_ERROR);
            }
        } else {
            JELogger.error(JEMessages.PROCESS_HAS_TO_BE_TRIGGERED_BY_EVENT,
                    LogCategory.RUNTIME, processes.get(id).getProjectId(),
                    LogSubModule.WORKFLOW, id);
        }


    }

    /*
     * Launch process by message event without variables
     * */
    public void launchProcessByMessageWithoutVariables(String messageId) {

        JEProcess workflow = null;
        try {
            for (JEProcess process : processes.values()) {
                if (process.isTriggeredByEvent() && process.getTriggerMessage().equals(messageId)) {
                    workflow = process;
                    Map<String, Object> variables = new HashMap<>();
                    for (ActivitiTask task : process.getActivitiTasks().values()) {
                        if (task instanceof InformTask) {
                            variables.put(task.getTaskId(), ((InformTask) task).getMessage());
                        }
                    }
                    process.setActiveThread(new Thread(() -> {
                        ProcessInstance p = runtimeService.startProcessInstanceByMessage(messageId, variables);
                        process.setRunning(true);
                    }));
                    process.getActiveThread().start();
                    break;
                }
            }
        } catch (ActivitiObjectNotFoundException e) {
            if (workflow != null) {
                JELogger.error(JEMessages.PROCESS_EXITED,
                        LogCategory.RUNTIME, workflow.getProjectId(),
                        LogSubModule.WORKFLOW, workflow.getKey());
            }
        } catch (Exception e) {
            JELogger.error("Error in launching a process by message " + Arrays.toString(e.getStackTrace()),
                    LogCategory.RUNTIME, workflow.getProjectId(),
                    LogSubModule.WORKFLOW, workflow.getKey());
        }
    }

    /*
     * Run all workflows ( in case runProject == false we run all kinds of workflows( scheduled or not ) )
     * */
    public void runAll(String projectId, boolean runProject) throws WorkflowNotFoundException, WorkflowBuildException {
        JELogger.debug(JEMessages.RUNNING_ALL_WORKFLOWS_IN_PROJECT_ID + " = " + projectId,
                LogCategory.RUNTIME, projectId,
                LogSubModule.WORKFLOW, null);
        for (JEProcess process : processes.values()) {
            if (process.getProjectId().equals(projectId) && process.isDeployed() && !process.isRunning()) {
                try {
                    launchProcessByKeyWithoutVariables(process.getKey(), runProject);
                } catch (WorkflowAlreadyRunningException e) {
                    JELogger.error(JEMessages.WORKFLOW_ALREADY_RUNNING + process.getKey(),
                            LogCategory.RUNTIME, projectId,
                            LogSubModule.WORKFLOW, process.getKey());
                } catch (WorkflowRunException e) {
                    JELogger.error(JEMessages.PROCESS_HAS_TO_BE_TRIGGERED_BY_EVENT + process.getKey(),
                            LogCategory.RUNTIME, projectId,
                            LogSubModule.WORKFLOW, process.getKey());
                } catch (WorkflowBuildException e) {
                    JELogger.error(JEMessages.WORKFLOW_RUN_ERROR + process.getKey(),
                            LogCategory.RUNTIME, projectId,
                            LogSubModule.WORKFLOW, process.getKey());
                }
            }
        }
    }

    public void buildProjectWorkflows(String projectId) throws WorkflowBuildException {
        JELogger.debug(JEMessages.BUILDING_WORKFLOWS_IN_PROJECT + " id = " + projectId,
                LogCategory.RUNTIME, projectId,
                LogSubModule.WORKFLOW, null);
        for (JEProcess process : processes.values()) {
            if (process.getProjectId().equals(projectId) && !process.isDeployed()) {
                deployProcess(process.getKey());
            }
        }
    }

    public void stopProjectWorkflows() {

        for (JEProcess process : processes.values()) {
            try {
                removeProcess(process.getKey());
            } catch (WorkflowRunException e) {
                JELogger.debug(JEMessages.FAILED_TO_STOP_THE_WORKFLOW_BECAUSE_IT_ALREADY_IS_STOPPED, LogCategory.RUNTIME, process.getProjectId(), LogSubModule.WORKFLOW, process.getKey());
            }
        }
    }

    public static void setRunning(String id, boolean b, String processInstanceId) {
        processes.get(id).setRunning(b);
        Status s = b? Status.RUNNING : Status.STOPPED;
        MonitoringMessage msg = new MonitoringMessage(LocalDateTime.now(), id, ObjectType.JEWORKFLOW,
                processes.get(id).getProjectId(), String.valueOf(b), s);
        JELogger.debug(SENDING_WORKFLOW_MONITORING_DATA_TO_JEMONITOR + "\n" + msg, LogCategory.RUNTIME, "", LogSubModule.WORKFLOW, processes.get(id).getName());
        JEMonitor.publish(msg);
    }


    //Stopr/remove workflow
    public void removeProcess(String workflowId) throws WorkflowRunException {
        JEProcess process = processes.get(workflowId);
        if (process != null && !process.isRunning()) {
            throw new WorkflowRunException("Workflow already stopped");
        }
        try {

            if (process != null) {
                process.getActiveThread().interrupt();
                JELogger.debug(JEMessages.STOPPING_WORKFLOW_FORCED,
                        LogCategory.RUNTIME, process.getProjectId(),
                        LogSubModule.WORKFLOW, workflowId);
            }

        } catch (ActivitiObjectNotFoundException e) {
            JELogger.error(JEMessages.ERROR_DELETING_A_NON_EXISTING_PROCESS,
                    LogCategory.RUNTIME, processes.get(workflowId).getProjectId(),
                    LogSubModule.WORKFLOW, workflowId);
        } catch (Exception e) {
            JELogger.error(JEMessages.ERROR_DELETING_A_PROCESS + "\n" + Arrays.toString(e.getStackTrace()),
                    LogCategory.RUNTIME, processes.get(workflowId).getProjectId(),
                    LogSubModule.WORKFLOW, workflowId);
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


}
