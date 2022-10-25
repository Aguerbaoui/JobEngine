package io.je.utilities.dataflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQRequester;

import java.util.HashMap;

public class DataflowRequester {

    public static ObjectMapper objectMapper = new ObjectMapper();
    private static ZMQRequester requester = new ZMQRequester(
            "tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
            SIOTHConfigUtility.getSiothConfig().getPorts().getDfResponsePort());

    public static ProjectManagementRequestResult sendRequest(Object req, boolean projMangReq) {

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
                if (projMangReq) {
                    respObject = objectMapper.readValue(response, ProjectManagementRequestResult.class);
                }
            }
        } catch (Exception e) {
            LoggerUtils.logException(e);
        }
        if (respObject == null && projMangReq) {
            JELogger.error("Project management api did not respond", LogCategory.DESIGN_MODE,
                    null, LogSubModule.JEBUILDER, null);

        }
        return respObject;
    }


    public static void sendDataflowZMQ(String projectId, String ruleId, HashMap<String, Object> requestParams) {
        Object response;
        try {
            response = sendRequest(requestParams, false);
            JELogger.control("Request sent succesfully", LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error("Failed to send request", LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);
            response = e.getMessage();
        }
    }

    public static void close() {
        if (requester != null) {
            requester.closeSocket();
            requester = null;
        }
    }
}
