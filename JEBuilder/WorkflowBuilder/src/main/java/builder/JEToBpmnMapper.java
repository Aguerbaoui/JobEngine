package builder;

import blocks.WorkflowBlock;
import blocks.basic.*;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.InclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.*;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.Timers;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.log.JELogger;
import models.JEWorkflow;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;

import java.util.ArrayList;

import static builder.ModelBuilder.getListener;
import static io.je.utilities.constants.JEMessages.BUILDING_BPMN_FROM_JEWORKFLOW;

public class JEToBpmnMapper {



    /*
     * private constructor
     * */
    private JEToBpmnMapper() {
    }

    /*
     * Generate and save bpmn workflow from JE workflow
     * */
    public static void createBpmnFromJEWorkflow( JEWorkflow wf) {

        JELogger.debug(BUILDING_BPMN_FROM_JEWORKFLOW + " " + wf.getJobEngineElementID(),
                LogCategory.DESIGN_MODE, wf.getJobEngineProjectID(),
                LogSubModule.WORKFLOW,wf.getJobEngineElementID());
        BpmnModel model = ModelBuilder.createNewBPMNModel();
        model.setTargetNamespace(wf.getJobEngineProjectID());
        Process process = ModelBuilder.createProcess(wf.getJobEngineElementName().trim());
        process.addFlowElement(
                ModelBuilder.createStartEvent(wf.getWorkflowStartBlock().getJobEngineElementID(),
                wf.getWorkflowStartBlock().getEventId(),
                wf.getWorkflowStartBlock().getTimerEvent()));
        addListeners(wf, process);
        parseWorkflowBlock(wf, wf.getWorkflowStartBlock(), process, null);
        model.addProcess(process);
        String modelPath = ConfigurationConstants.BPMN_PATH + wf.getJobEngineElementName().trim() + WorkflowConstants.BPMN_EXTENSION;
        ModelBuilder.saveModel(model, modelPath);
        wf.resetBlocks();
        wf.setBpmnPath(modelPath);
    }

    /*
     * Set the start and end execution listeners for the workflow
     * */
    private static void addListeners(JEWorkflow workflow, Process process) {
        JELogger.debug(JEMessages.ADDING_LISTENERS_TO_PROCESS + " id = " + process.getName(),
                LogCategory.DESIGN_MODE, workflow.getJobEngineProjectID(),
                LogSubModule.WORKFLOW,workflow.getJobEngineElementID());
        ArrayList<ActivitiListener> listeners = new ArrayList<ActivitiListener>();
        listeners.add(getListener(WorkflowConstants.PROCESS_LISTENER_IMPLEMENTATION, WorkflowConstants.START_PROCESS, ImplementationType.IMPLEMENTATION_TYPE_CLASS));
        listeners.add(getListener(WorkflowConstants.PROCESS_LISTENER_IMPLEMENTATION, WorkflowConstants.END_PROCESS, ImplementationType.IMPLEMENTATION_TYPE_CLASS));
        ModelBuilder.setListenersForProcess(process, listeners);

    }

    /*
     * Parse job engine blocks to bpmn blocks
     * */
    private static void parseWorkflowBlock(JEWorkflow wf, WorkflowBlock startBlock, Process process, WorkflowBlock previous) {
        if (previous != null) {
            process.addFlowElement(ModelBuilder.createSequenceFlow(previous.getJobEngineElementID(), startBlock.getJobEngineElementID(), previous.getCondition()));
        }
        if (startBlock.isProcessed()) return;
        JELogger.debug(JEMessages.PROCESSING_BLOCK_NAME + " = " + startBlock.getJobEngineElementName() +
                        " in workflow" + " = " + wf.getJobEngineElementName(),
                LogCategory.DESIGN_MODE, wf.getJobEngineProjectID(),
                LogSubModule.WORKFLOW,wf.getJobEngineElementID());
        startBlock.setProcessed(true);
        for (String id : startBlock.getOutFlows().values()) {
            WorkflowBlock block = wf.getBlockById(id);
            if (block instanceof EndBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createEndEvent(block.getJobEngineElementID()));
            }

            else if (block instanceof ParallelGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createParallelGateway(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        block.generateBpmnInflows(wf), block.generateBpmnOutflows(wf)));
            }

            else if (block instanceof MessageEvent && !block.isProcessed() && !((MessageEvent) block).isThrowMessage()) {
				process.addFlowElement(ModelBuilder.createMessageIntermediateCatchEvent(block.getJobEngineElementID(), block.getJobEngineElementName(),
						((MessageEvent) block).getEventId()));
			}

            else if (block instanceof SignalEvent && !block.isProcessed() && !((SignalEvent) block).isThrowSignal()) {
                process.addFlowElement(ModelBuilder.createSignalIntermediateCatchEvent(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        ((SignalEvent) block).getEventId()));
            }

            else if (block instanceof MessageEvent && !block.isProcessed() && ((MessageEvent) block).isThrowMessage()) {
                process.addFlowElement(ModelBuilder.createThrowMessageEvent(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        ((MessageEvent) block).getEventId()));
            }

            else if (block instanceof SignalEvent && !block.isProcessed() && ((SignalEvent) block).isThrowSignal()) {
                process.addFlowElement(ModelBuilder.createThrowSignalEvent(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        ((SignalEvent) block).getEventId()));
            }

            else if(block instanceof ErrorBoundaryEvent && !block.isProcessed()) {
                ServiceTask attachedTo = null;
                for(FlowElement f: process.getFlowElements()) {
                    if(f.getId().equals(((ErrorBoundaryEvent) block).getAttachedToRef())){
                        attachedTo = (ServiceTask) f; break;
                    }
                }
                process.addFlowElement(ModelBuilder.createBoundaryEvent(block.getJobEngineElementID(),
                        ((ErrorBoundaryEvent) block).getAttachedToRef(), attachedTo,
                        ((ErrorBoundaryEvent) block).getErrorRef()));
            }
            else if (block instanceof ScriptBlock && !block.isProcessed()) {
                ServiceTask serviceTask = ModelBuilder.createServiceTask(block.getJobEngineElementID(), wf.getJobEngineElementName()+block.getJobEngineElementName(),
                        WorkflowConstants.SCRIPT_TASK_IMPLEMENTATION);
                process.addFlowElement(serviceTask);
            }

            else if (block instanceof ExclusiveGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createExclusiveGateway(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        ((ExclusiveGatewayBlock) block).isExclusive(), block.generateBpmnInflows(wf),
                        block.generateBpmnOutflows(wf)));
            }

            else if (block instanceof DBWriteBlock || block instanceof DBEditBlock || block instanceof DBReadBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        WorkflowConstants.DB_TASK_IMPLEMENTATION));
            }

            else if (block instanceof MailBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        WorkflowConstants.MAIL_TASK_IMPLEMENTATION));
            }

            else if (block instanceof EventGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createEventGateway(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        ((EventGatewayBlock) block).isExclusive(), block.generateBpmnInflows(wf),
                        block.generateBpmnOutflows(wf)));
            }

            else if (block instanceof InclusiveGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createInclusiveGateway(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        ((InclusiveGatewayBlock) block).isExclusive(), block.generateBpmnInflows(wf),
                        block.generateBpmnOutflows(wf)));
            }

            else if (block instanceof TimerEvent && !block.isProcessed()) {
                if(((TimerEvent) block).getTimer() == Timers.DATE_TIME) {
                    process.addFlowElement(ModelBuilder.createDateTimerEvent(block.getJobEngineElementID(), block.getJobEngineElementName(), ((TimerEvent) block).getTimeDate()));
                }
                else if(((TimerEvent) block).getTimer() == Timers.CYCLIC) {
                    process.addFlowElement(ModelBuilder.createCycleTimerEvent(block.getJobEngineElementID(), block.getJobEngineElementName(), ((TimerEvent) block).getTimeCycle(), ((TimerEvent) block).getEndDate()));
                }
                else {
                    process.addFlowElement(ModelBuilder.createDurationTimerEvent(block.getJobEngineElementID(), block.getJobEngineElementName(), ((TimerEvent) block).getTimeDuration()));
                }
            }

            /*else if (block instanceof CycleTimerEvent && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createCycleTimerEvent(block.getJobEngineElementID(), block.getName(), ((CycleTimerEvent) block).getTimeCycle(), ((CycleTimerEvent) block).getEndDate()));
            }

            else if (block instanceof DurationDelayTimerEvent && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createDurationTimerEvent(block.getJobEngineElementID(), block.getName(), ((DurationDelayTimerEvent) block).getTimeDuration()));
            }*/

            else if (block instanceof WebApiBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        WorkflowConstants.WEB_TASK_IMPLEMENTATION));
            }

            else if (block instanceof InformBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getJobEngineElementName(),
                        WorkflowConstants.INFORM_TASK_IMPLEMENTATION));
            }

            else if(block instanceof SubProcessBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createCallActivity(block.getJobEngineElementID(), block.getJobEngineElementName(), ((SubProcessBlock) block).getSubWorkflowId()));
            }
            parseWorkflowBlock(wf, block, process, startBlock);
        }
    }

}
