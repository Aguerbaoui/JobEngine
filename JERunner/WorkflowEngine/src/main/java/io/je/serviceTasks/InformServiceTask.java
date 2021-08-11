package io.je.serviceTasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.je.utilities.beans.JEBlockMessage;
import io.je.utilities.beans.JEMessage;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.execution.Executioner;
import io.je.utilities.logger.*;
import org.activiti.engine.delegate.DelegateExecution;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

public class InformServiceTask extends ServiceTask{
    @Override
    public void execute(DelegateExecution execution) {
        if(execution.getVariable(execution.getCurrentActivityId()) != null) {
            InformTask informTask = (InformTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
            JELogger.info(JEMessages.INFORM_FROM_USER + " id = " + execution.getVariable(execution.getCurrentActivityId()));
            /*JEMessage message = new JEMessage();
            message.setExecutionTime(LocalDate.now().toString());
            message.setType("Workflow");
            JEBlockMessage blockMessage = new JEBlockMessage(informTask.getTaskName(), informTask.getMessage());*/

            LogMessage msg = new LogMessage(LogLevel.INFORM,  informTask.getMessage(),  LocalDateTime.now().toString(), "JobEngine",  informTask.getProjectId(),
                    informTask.getProcessId(), LogSubModule.WORKFLOW, informTask.getTaskName(), null, "Log", "") ;

            //message.getBlocks().add(blockMessage);
            try {
                ZMQLogPublisher.publish(msg);
                //JELogger.info(Executioner.objectMapper.writeValueAsString(message), LogCategory.RUNTIME, informTask.getProjectId(), LogSubModule.WORKFLOW, execution.getProcessDefinitionId());
            } catch (Exception e) {
                JELogger.error(Arrays.toString(e.getStackTrace()));
            }
        }
    }
}
