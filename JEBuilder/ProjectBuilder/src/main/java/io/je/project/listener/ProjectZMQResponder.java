package io.je.project.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.project.beans.project.request.ProjectRequestObject;
import io.je.project.beans.project.response.CleanUpResponseModel;
import io.je.project.exception.JEExceptionHandler;
import io.je.project.services.ProjectService;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQType;
import utils.zmq.ZMQResponder;

/**
 * Project management api ZMQ Responder
 */
@Component
@Lazy
public class ProjectZMQResponder extends ZMQResponder {

    @Autowired
    @Lazy
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    @Lazy
    ProjectService projectService;



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

                String data = this.getResponderSocket(ZMQType.BIND)
                        .recvStr(0);

                if (data != null && !data.isEmpty() && !data.equals("null")) {

                    JELogger.info("Received ZMQ request: " + data, null, null, LogSubModule.JEBUILDER, null);

                    request = objectMapper.readValue(data, ProjectRequestObject.class);

                    switch (request.getType()) {
                        case Build:
                            break;
                        case CLEANALL:
                            response = cleanAll();
                            break;
                        case Delete:
                            break;
                        case GetInfo:
                            break;
                        case Start:
                            break;
                        case Stop:
                            break;
                        case Update:
                            break;
                        default:
                            break;

                    }

                    LoggerUtils.trace("ProjectZMQResponder : " + url + ":" + responderPort + " : sending : " + response);

                    sendResponse(response);
                }

            } catch (Exception e) {
                String errorMsg = JEExceptionHandler.getExceptionMessage(e);
                JELogger.error("Failed to send ZMQ response: " + errorMsg, null, null, LogSubModule.JEBUILDER, null);

            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LoggerUtils.logException(e);
            }
        }

    }

    /*
     * clean all Job Engine collections
     */
    private String cleanAll() {
        CleanUpResponseModel response = new CleanUpResponseModel();
        response.setComponentName("Job Engine");
        //clean up project
        try {
            projectService.cleanUpHouse();
            response.setResult(true);
            response.setStrError("");
        } catch (Exception e) {
            JELogger.error("Failed to clean all: " + e.getMessage(), null, null, LogSubModule.JEBUILDER, null);
            response.setResult(false);
            response.setStrError(JEExceptionHandler.getExceptionMessage(e));
        }
        //generate response string
        String responseStr = "";

        try {
            responseStr = objectMapper.writeValueAsString(response);

        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error("Failed to generate response : " + e.getMessage(), null, null, LogSubModule.JEBUILDER, null);
        }

        return responseStr;
    }

    private void sendResponse(String response) {

        try {
            JELogger.debug("Sending response : " + response, null, null, LogSubModule.JEBUILDER, null);

            this.getResponderSocket(ZMQType.BIND)
                    .send(response);
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error("Failed to send ZMQ response : " + e.getMessage(), null, null, LogSubModule.JEBUILDER, null);
        }

    }

}
