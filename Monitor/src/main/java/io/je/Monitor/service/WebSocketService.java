package io.je.Monitor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.monitoring.MonitoringMessage;
import io.je.utilities.monitoring.ObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
                template.convertAndSend("/rule/ruleUpdates", payload);
            }
            else if (msg.getObjectType().equals(ObjectType.JEWORKFLOW)) {
                template.convertAndSend("/workflow/workflowUpdates", payload);
            }
            else if (msg.getObjectType().equals(ObjectType.JEEVENT)) {
                template.convertAndSend("/event/eventUpdates", payload);
            }
            else {
                template.convertAndSend("/variable/variableUpdates", payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
