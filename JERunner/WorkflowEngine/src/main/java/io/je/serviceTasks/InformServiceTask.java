package io.je.serviceTasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.je.utilities.beans.JEBlockMessage;
import io.je.utilities.beans.JEMessage;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.execution.Executioner;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import org.activiti.engine.delegate.DelegateExecution;

import java.time.LocalDate;

public class InformServiceTask extends ServiceTask{
    @Override
    public void execute(DelegateExecution execution) {
        if(execution.getVariable(execution.getCurrentActivityId()) != null) {
            InformTask informTask = (InformTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
            JELogger.info(JEMessages.INFORM_FROM_USER + " = " + execution.getVariable(execution.getCurrentActivityId()));
            JEMessage message = new JEMessage();
            message.setExecutionTime(LocalDate.now().toString());
            message.setType("Workflow");
            JEBlockMessage blockMessage = new JEBlockMessage(informTask.getTaskName(), informTask.getMessage());
            message.getBlocks().add(blockMessage);
            try {
                JELogger.info(Executioner.objectMapper.writeValueAsString(message), LogCategory.RUNTIME, informTask.getProjectId(), LogSubModule.WORKFLOW, execution.getProcessDefinitionId());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
