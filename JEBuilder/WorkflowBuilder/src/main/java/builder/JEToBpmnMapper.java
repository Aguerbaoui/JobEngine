package builder;

import blocks.WorkflowBlock;
import blocks.basic.*;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.InclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.*;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.logger.JELogger;
import models.JEWorkflow;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;

import java.util.ArrayList;

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

        JELogger.trace(" Building bpmn from jeworkflow id = " + wf.getJobEngineElementID());
        BpmnModel model = ModelBuilder.createNewBPMNModel();
        model.setTargetNamespace(wf.getJobEngineProjectID());
        Process process = ModelBuilder.createProcess(wf.getWorkflowName().trim());
        process.addFlowElement(ModelBuilder.createStartEvent(wf.getWorkflowStartBlock().getJobEngineElementID(), wf.getWorkflowStartBlock().getEventId()));
        addListeners(process);
        parseWorkflowBlock(wf, wf.getWorkflowStartBlock(), process, null);
        model.addProcess(process);
        String modelPath = WorkflowConstants.BPMN_PATH + wf.getWorkflowName().trim() + WorkflowConstants.BPMN_EXTENSION;
        ModelBuilder.saveModel(model, modelPath);
        wf.resetBlocks();
        wf.setBpmnPath(modelPath);
    }

    /*
     * Set the start and end execution listeners for the workflow
     * */
    private static void addListeners(Process process) {
        JELogger.trace(" Adding listeners to process id = " + process.getName());
        ActivitiListener startProcessListener = new ActivitiListener();
        startProcessListener.setImplementation(WorkflowConstants.PROCESS_LISTENER_IMPLEMENTATION);
        startProcessListener.setEvent(WorkflowConstants.START_PROCESS);
        startProcessListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        ActivitiListener endProcessListener = new ActivitiListener();
        endProcessListener.setImplementation(WorkflowConstants.PROCESS_LISTENER_IMPLEMENTATION);
        endProcessListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        endProcessListener.setEvent(WorkflowConstants.END_PROCESS);
        ArrayList<ActivitiListener> listeners = new ArrayList<ActivitiListener>();
        listeners.add(startProcessListener);
        listeners.add(endProcessListener);
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
        JELogger.trace(" Processing block name = " + startBlock.getName() + " in workflow name = " + wf.getWorkflowName());
        startBlock.setProcessed(true);
        for (String id : startBlock.getOutFlows().values()) {
            WorkflowBlock block = wf.getBlockById(id);
            if (block instanceof EndBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createEndEvent(block.getJobEngineElementID()));
            }

            else if (block instanceof ParallelGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createParallelGateway(block.getJobEngineElementID(), block.getName(),
                        block.generateBpmnInflows(wf), block.generateBpmnOutflows(wf)));
            }

            else if (block instanceof MessageEvent && !block.isProcessed() && !((MessageEvent) block).isThrowMessage()) {
				process.addFlowElement(ModelBuilder.createMessageIntermediateCatchEvent(block.getJobEngineElementID(), block.getName(),
						((MessageEvent) block).getEventId()));
			}

            else if (block instanceof SignalEvent && !block.isProcessed() && !((SignalEvent) block).isThrowSignal()) {
                process.addFlowElement(ModelBuilder.createSignalIntermediateCatchEvent(block.getJobEngineElementID(), block.getName(),
                        ((SignalEvent) block).getEventId()));
            }

            else if (block instanceof MessageEvent && !block.isProcessed() && ((MessageEvent) block).isThrowMessage()) {
                process.addFlowElement(ModelBuilder.createThrowMessageEvent(block.getJobEngineElementID(), block.getName(),
                        ((MessageEvent) block).getEventId()));
            }

            else if (block instanceof SignalEvent && !block.isProcessed() && ((SignalEvent) block).isThrowSignal()) {
                process.addFlowElement(ModelBuilder.createThrowSignalEvent(block.getJobEngineElementID(), block.getName(),
                        ((SignalEvent) block).getEventId()));
            }

            else if (block instanceof ScriptBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getName(),
                        WorkflowConstants.SCRIPT_TASK_IMPLEMENTATION));
            }

            else if (block instanceof ExclusiveGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createExclusiveGateway(block.getJobEngineElementID(), block.getName(),
                        ((ExclusiveGatewayBlock) block).isExclusive(), block.generateBpmnInflows(wf),
                        block.generateBpmnOutflows(wf)));
            }

            else if (block instanceof DBWriteBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getName(),
                        WorkflowConstants.DB_WRITE_TASK_IMPLEMENTATION));
            }

            else if (block instanceof MailBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getName(),
                        WorkflowConstants.MAIL_TASK_IMPLEMENTATION));
            }

            else if (block instanceof EventGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createEventGateway(block.getJobEngineElementID(), block.getName(),
                        ((EventGatewayBlock) block).isExclusive(), block.generateBpmnInflows(wf),
                        block.generateBpmnOutflows(wf)));
            }

            else if (block instanceof InclusiveGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createInclusiveGateway(block.getJobEngineElementID(), block.getName(),
                        ((InclusiveGatewayBlock) block).isExclusive(), block.generateBpmnInflows(wf),
                        block.generateBpmnOutflows(wf)));
            }

            else if (block instanceof DateTimerEvent && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createDateTimerEvent(block.getJobEngineElementID(), block.getName(), ((DateTimerEvent) block).getTimeDate()));
            }

            else if (block instanceof CycleTimerEvent && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createCycleTimerEvent(block.getJobEngineElementID(), block.getName(), ((CycleTimerEvent) block).getTimeCycle(), ((CycleTimerEvent) block).getEndDate()));
            }

            else if (block instanceof DurationDelayTimerEvent && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createDateTimerEvent(block.getJobEngineElementID(), block.getName(), ((DurationDelayTimerEvent) block).getTimeDuration()));
            }

            else if (block instanceof WebApiBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getName(),
                        WorkflowConstants.WEB_TASK_IMPLEMENTATION));
            }

            parseWorkflowBlock(wf, block, process, startBlock);
        }
    }

}
