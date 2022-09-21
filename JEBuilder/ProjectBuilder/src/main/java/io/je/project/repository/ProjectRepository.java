package io.je.project.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.project.beans.JEProject;
import io.je.project.beans.project.sentrequest.ExchangedUpdatedObjectAttributeNames;
import io.je.project.beans.project.sentrequest.request.ExchangedUpdateObject;
import io.je.project.beans.project.sentrequest.request.RequestType;
import io.je.project.beans.project.sentrequest.response.ProjectManagementRequestResult;
import io.je.project.beans.project.sentrequest.response.ProjectModel;
import io.je.project.exception.JEExceptionHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ProjectLoadException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQRequester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//TOOD: add logs
public class ProjectRepository {

    public static ObjectMapper objectMapper = new ObjectMapper();
    private static ZMQRequester requester = new ZMQRequester(
            "tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
            SIOTHConfigUtility.getSiothConfig().getPorts().getDfResponsePort());
    private static Map<String, JEProject> projects = new ConcurrentHashMap<>();

    /*
     * request to get project }
     */
    public static JEProject getProject(String projectId) throws ProjectNotFoundException {

        JEProject project;
        if (!projects.containsKey(projectId)) {
            project = requestProjectDefinition(projectId, true);
            if (project == null) {
                JELogger.error(JEMessages.PROJECT_NOT_FOUND, LogCategory.DESIGN_MODE,
                        null, LogSubModule.JEBUILDER, null);
                throw new ProjectNotFoundException("[projectId=" + projectId + "project not found.");
            }
            projects.put(projectId, project);
            return project;
        }
        return projects.get(projectId);
    }

    public static JEProject requestProjectDefinition(String projectKey, boolean byId) throws ProjectNotFoundException {
        JEProject project = null;
        ProjectManagementRequestResult response;
        HashMap<String, Object> req = new HashMap<>();
        req.put("type", RequestType.GET);
        req.put("isGetById", byId);
        req.put("key", projectKey);
        try {

            response = sendRequest(req);
            if (response == null || !response.isOk || response.projects.get(0) == null) {
                throw new ProjectNotFoundException("Project management api did not respond");
            }
            project = createProject(response.projects.get(0));

        } catch (Exception e) {
            throw new ProjectNotFoundException("[projectId=" + projectKey + "]Failed to load project : "
                    + JEExceptionHandler.getExceptionMessage(e));

        }
        return project;
    }

    private static ProjectManagementRequestResult sendRequest(Object req) {

        String request = "";
        String response = "";
        ProjectManagementRequestResult respObject = new ProjectManagementRequestResult();
        try {
            synchronized (requester) {
                // Generate request
                request = objectMapper.writeValueAsString(req);
                JELogger.debugWithoutPublish("Sending request to project management api " + request, LogCategory.DESIGN_MODE,
                        null, LogSubModule.JEBUILDER, null);
                response = requester.sendRequest(request);
                respObject = objectMapper.readValue(response, ProjectManagementRequestResult.class);

            }
        } catch (Exception e) {
            LoggerUtils.logException(e);
            respObject.isOk = false;
        }
        if (respObject == null || !respObject.isOk) {
            JELogger.error("Project management api did not respond", LogCategory.DESIGN_MODE,
                    null, LogSubModule.JEBUILDER, null);

        }
        return respObject;
    }

    private static JEProject createProject(ProjectModel projectModel) {
        JEProject project = new JEProject(projectModel.get_id());
        project.setProjectName(projectModel.getKey());
        project.setBuilt(projectModel.isBuilt());
        project.setRunning(projectModel.isRunning());
        project.setBlockNameCounters(projectModel.getBlockNameCounters());
        project.setBlockNames(projectModel.getBlockNames());
        project.setConfigurationPath(projectModel.getConfigurationPath());
        project.setDescription(projectModel.getDescription());
        project.setAutoReload(projectModel.isAutoReload());

        return project;
    }

    public static List<JEProject> getAllProjects(boolean onlyAutoReloadProjects) {
        List<JEProject> allProjects = new ArrayList<JEProject>();
        ProjectManagementRequestResult response;
        HashMap<String, Object> req = new HashMap<>();

        if (onlyAutoReloadProjects) {
            req.put("type", RequestType.GETAUTORELOAD);
        } else {
            req.put("type", RequestType.GETALL);

        }

        try {
            response = sendRequest(req);
            if (response == null || !response.isOk) {

                throw new ProjectNotFoundException("Project management api did not respond");
            }
            if (!(response.projects == null)) {
                for (ProjectModel project : response.projects) {
                    JEProject jeProject = createProject(project);
                    allProjects.add(jeProject);
                    projects.put(jeProject.getProjectId(), jeProject); //? populating the in memory variable
                }
            }


        } catch (Exception e) {
            LoggerUtils.logException(e);


        }
        return allProjects;
    }

    /*
     * delete project
     */
    public static void deleteProject(String projectId) {
        if (projects.containsKey(projectId)) {
            projects.remove(projectId);
        }

    }

    /*
     * update project
     */
    public static void updateProject(String projectId, Map<String, String> valuesToUpdate) {
        if (projects.containsKey(projectId)) {
            if (valuesToUpdate.containsKey(ExchangedUpdatedObjectAttributeNames.AUTO_RELOAD)) {
                projects.get(projectId).setAutoReload(
                        Boolean.getBoolean(valuesToUpdate.get(ExchangedUpdatedObjectAttributeNames.AUTO_RELOAD)));

            }
            if (valuesToUpdate.containsKey(ExchangedUpdatedObjectAttributeNames.PROJECT_NAME)) {
                projects.get(projectId).setProjectName(
                        valuesToUpdate.get(ExchangedUpdatedObjectAttributeNames.PROJECT_NAME));

            }
            if (valuesToUpdate.containsKey(ExchangedUpdatedObjectAttributeNames.PROJECT_DESCRIPTION)) {
                projects.get(projectId).setDescription(
                        valuesToUpdate.get(ExchangedUpdatedObjectAttributeNames.PROJECT_DESCRIPTION));

            }
        }

    }

    public static boolean projectExists(String projectId) {
        return projects.containsKey(projectId);
    }

    public static void deleteAll() {
        projects.clear();

    }

    public static List<JEProject> getAllLoadedProjects() {
        return (List<JEProject>) projects.values();
    }

    public static void saveProject(String projectId) throws ProjectLoadException {
        JEProject project = projects.get(projectId);
        ExchangedUpdateObject updateModel = new ExchangedUpdateObject();
        synchronized (project) {
            updateModel.setProjectId(projectId);
            updateModel.setAddedblockNames(project.addedblockNames);
            updateModel.setDeletedBlockNames(project.deletedBlockNames);
            updateModel.setBlockNameCounters(project.getBlockNameCounters());
            updateModel.setBuilt(project.isBuilt());
            updateModel.setRunning(project.isRunning());
            updateModel.setAutoReload(project.isAutoReload());
            updateModel.setConfigurationPath(project.getConfigurationPath());
            ProjectManagementRequestResult response = sendRequest(updateModel);
            if (!response.isOk) {
                throw new ProjectLoadException("Failed to save project.");
            }
            project.clearTempLists();

        }

    }

}
