package io.je.Monitor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.monitoring.MonitoringMessage;
import io.je.utilities.monitoring.ObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import utils.log.LoggerUtils;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate template;
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public WebSocketService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendUpdates(String payload) {
        try {
            MonitoringMessage msg = objectMapper.readValue(payload, MonitoringMessage.class);
            if (msg.getObjectType().equals(ObjectType.JERULE)) {
                template.convertAndSend("/rule/ruleUpdates", msg);
            } else if (msg.getObjectType().equals(ObjectType.JEWORKFLOW)) {
                template.convertAndSend("/workflow/workflowUpdates", msg);
                WorkflowModel m = new WorkflowModel();
                m.setProjectId(msg.getObjectProjectId());
                m.setStatus(msg.getStatus());
                m.setId(msg.getObjectId());
                JEBuilderApiHandler.updateWorkflowStatus(msg.getObjectId(), msg.getObjectProjectId(), m);
            } else if (msg.getObjectType().equals(ObjectType.JEEVENT)) {
                template.convertAndSend("/event/eventUpdates", msg);
            } else {
                template.convertAndSend("/variable/variableUpdates", msg);
            }
        } catch (Exception e) {
            LoggerUtils.logException(e);
        }
    }
}
