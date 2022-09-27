package io.je.project.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.project.beans.JEProject;
import io.je.project.beans.project.receivedrequest.request.ProjectRequestObject;
import io.je.project.beans.project.receivedrequest.response.CleanUpResponseModel;
import io.je.project.beans.project.receivedrequest.response.ProjectRequestResult;
import io.je.project.beans.project.receivedrequest.response.RequestGetInfoResult;
import io.je.project.exception.JEExceptionHandler;
import io.je.project.repository.ProjectRepository;
import io.je.project.services.ProjectService;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.log.JELogger;
import io.je.utilities.ruleutils.OperationStatusDetails;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.log.LogSubModule;
import utils.zmq.ZMQResponder;
import utils.zmq.ZMQType;

import java.util.HashMap;
import java.util.List;

/**
 * Project management api ZMQ Responder
 */
@Component
public class ProjectZMQResponder extends ZMQResponder {

    @Autowired
    ProjectService projectService;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    public ProjectZMQResponder() {
        super("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
                SIOTHConfigUtility.getSiothConfig().getPorts().getJeResponsePort(), ZMQType.BIND);
    }

    @Override
    public void run() {

        while (isListening()) {
            String response = "";
            ProjectRequestObject request = new ProjectRequestObject();
            try {
                String data = this.getResponderSocket(ZMQType.BIND).recvStr(0);
                if (data != null && !data.isEmpty() && !data.equals("null")) {

                    JELogger.info("Received ZMQ request: " + data, null, null, LogSubModule.JEBUILDER, null);

                    request = objectMapper.readValue(data, ProjectRequestObject.class);

                    switch (request.getType()) {
                        case CLEANALL:
                            response = cleanAll();
                            break;
                        case Build:
                            response = buildProject(request.getProjectId());
                            break;
                        case Delete:
                            response = deleteProject(request.getProjectId());
                            break;
                        case Start:
                            response = startProject(request.getProjectId());
                            break;
                        case Stop:
                            response = stopProject(request.getProjectId());
                            break;
                        case Update:
                            response = updateProject(request);
                            break;
                        case GetInfo:
                            response = String.valueOf(getProjectInfo(request.getProjectId()));
                            break;
                        default:
                            response = "Unknown Request : " + request;

                    }
                    sendResponse(response);
                }

            } catch (Exception e) {
                String errorMsg = JEExceptionHandler.getExceptionMessage(e);
                JELogger.error("Failed to send ZMQ response: " + errorMsg, null, null, LogSubModule.JEBUILDER, null);

            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private String startProject(String projectId) throws ProjectNotFoundException {
        ProjectRequestResult response = new ProjectRequestResult();
        try {
            projectService.runAll(projectId);


        } catch (Exception e) {
            response.setStrError(e.getMessage());

        }

        return getProjectResponse(response, projectId);
    }

    private String updateProject(ProjectRequestObject request) throws ProjectNotFoundException {
        ProjectRequestResult response = new ProjectRequestResult();
        try {
            ProjectRepository.updateProject(request.getProjectId(), request.getDicOfValues());


        } catch (Exception e) {
            response.setStrError(e.getMessage());

        }

        return getProjectResponse(response, request.getProjectId());
    }

    private String stopProject(String projectId) throws ProjectNotFoundException {
        ProjectRequestResult response = new ProjectRequestResult();
        try {
            projectService.stopProject(projectId);

        } catch (Exception e) {
            response.setStrError(e.getMessage());

        }

        return getProjectResponse(response, projectId);
    }

    private String deleteProject(String projectId) throws ProjectNotFoundException {
        ProjectRequestResult response = new ProjectRequestResult();
        try {
            projectService.removeProject(projectId).get();
            response.setDeleted(true);


        } catch (Exception e) {
            response.setStrError(e.getMessage());

        }

        return getResponse(response);
    }

    private String buildProject(String projectId) throws ProjectNotFoundException {
        ProjectRequestResult response = new ProjectRequestResult();
        try {
            List<OperationStatusDetails> operationStatusDetails = projectService.buildAll(projectId);
            response.setResult(operationStatusDetails);


        } catch (Exception e) {
            response.setStrError(e.getMessage());

        }

        return getProjectResponse(response, projectId);
    }

    private String getProjectInfo(String projectId) throws ProjectNotFoundException {
        HashMap<String, Integer> data = new HashMap<>();
        ProjectRequestResult response = new ProjectRequestResult();
        try {
            var project = projectService.getProject(projectId);
            if (project != null) {
                var requestGetInfoResult = new RequestGetInfoResult();
                requestGetInfoResult.setDataflowCount(0);
                requestGetInfoResult.setRuleCount(project.getRules().size());
                requestGetInfoResult.setWorkflowCount(project.getWorkflows().size());
                requestGetInfoResult.setEventCount(project.getEvents().size());
                response.setGetInfoResult(requestGetInfoResult);

            }
        } catch (Exception e) {

        }
        return getProjectResponse(response, projectId);

    }

    /*
     * clean all Job Engine collections
     */
    private String cleanAll() {
        CleanUpResponseModel response = new CleanUpResponseModel();
        response.setComponentName("Job Engine");
        // clean up project
        try {
            projectService.cleanUpHouse();
            response.setResult(true);
            response.setStrError("");
        } catch (Exception e) {
            JELogger.error("Failed to clean all: " + e.getMessage(), null, null, LogSubModule.JEBUILDER, null);
            response.setResult(false);
            response.setStrError(JEExceptionHandler.getExceptionMessage(e));
        }
        return getResponse(response);
    }

    private void sendResponse(String response) {

        try {
            JELogger.debug("Sending response: " + response, null, null, LogSubModule.JEBUILDER, null);

            this.getResponderSocket(ZMQType.BIND).send(response);
        } catch (Exception e) {
            JELogger.error("Failed to send ZMQ response: " + e.getMessage(), null, null, LogSubModule.JEBUILDER, null);
        }

    }

    private String getResponse(Object respObject) {
        // generate response string
        String responseStr = "";

        try {
            responseStr = objectMapper.writeValueAsString(respObject);

        } catch (Exception e) {
            JELogger.error("Failed to generate response: " + e.getMessage(), null, null, LogSubModule.JEBUILDER, null);

        }

        return responseStr;
    }

    private String getProjectResponse(ProjectRequestResult response, String projectId) throws ProjectNotFoundException {

        JEProject project = ProjectRepository.getProject(projectId);
        response.setBuilt(project.isBuilt());
        response.setRunning(project.isRunning());
        response.setStopped(!project.isRunning());
        response.setUpdated(true);
        return getResponse(response);
    }

}
