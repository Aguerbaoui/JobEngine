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
import io.je.utilities.network.Network;
import models.JEWorkflow;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
        BpmnModel model = ModelBuilder.createNewBPMNModel();
        model.setTargetNamespace(wf.getJobEngineProjectID());
        Process process = ModelBuilder.createProcess(wf.getWorkflowName().trim());
        process.addFlowElement(ModelBuilder.createStartEvent(wf.getWorkflowStartBlock().getJobEngineElementID(), wf.getWorkflowStartBlock().getEventId()));
        addListeners(process);
        parseWorkflowBlock(wf, wf.getWorkflowStartBlock(), process, null);
//TODO send bpmn task information since they are dynamically created
        model.addProcess(process);
        //new BpmnAutoLayout(model).execute();
        String modelPath = WorkflowConstants.BPMN_PATH + wf.getWorkflowName().trim() + WorkflowConstants.BPMN_EXTENSION;
        ModelBuilder.saveModel(model, modelPath);
        wf.resetBlocks();
        wf.setBpmnPath(modelPath);
    }

    /*
     * Set the start and end execution listeners for the workflow
     * */
    private static void addListeners(Process process) {
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
        startBlock.setProcessed(true);
        for (String id : startBlock.getOutFlows().values()) {
            WorkflowBlock block = wf.getBlockById(id);
            if (block instanceof EndBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createEndEvent(block.getJobEngineElementID()));
            } else if (block instanceof ParallelGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createParallelGateway(block.getJobEngineElementID(), block.getName(),
                        block.generateBpmnInflows(wf), block.generateBpmnOutflows(wf)));
            } else if (block instanceof MessageCatchEvent && !block.isProcessed()) {
				process.addFlowElement(ModelBuilder.createMessageIntermediateCatchEvent(block.getJobEngineElementID(), block.getName(),
						((MessageCatchEvent) block).getEventId()));
			} else if (block instanceof SignalCatchEvent && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createSignalIntermediateCatchEvent(block.getJobEngineElementID(), block.getName(),
                        ((SignalCatchEvent) block).getEventId()));
            }else if (block instanceof ThrowMessageEvent && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createThrowMessageEvent(block.getJobEngineElementID(), block.getName(),
                        ((ThrowMessageEvent) block).getEventId()));
            }else if (block instanceof ThrowSignalEvent && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createThrowSignalEvent(block.getJobEngineElementID(), block.getName(),
                        ((ThrowSignalEvent) block).getEventId()));
            }else if (block instanceof ScriptBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createScriptTask(block.getJobEngineElementID(), block.getName(),
                        ((ScriptBlock) block).getScript()));
            } else if (block instanceof ExclusiveGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createExclusiveGateway(block.getJobEngineElementID(), block.getName(),
                        ((ExclusiveGatewayBlock) block).isExclusive(), block.generateBpmnInflows(wf),
                        block.generateBpmnOutflows(wf)));
            } else if (block instanceof DBWriteBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getName(),
                        WorkflowConstants.DB_WRITE_TASK_IMPLEMENTATION));
            } else if (block instanceof MailBlock && !block.isProcessed()) {
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

            parseWorkflowBlock(wf, block, process, startBlock);
        }
    }

}
