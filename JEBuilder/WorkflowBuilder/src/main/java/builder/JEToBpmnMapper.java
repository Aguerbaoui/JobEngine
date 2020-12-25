package builder;

import blocks.WorkflowBlock;
import blocks.basic.DBWriteBlock;
import blocks.basic.EndBlock;
import blocks.basic.MailBlock;
import blocks.basic.ScriptBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;
import models.JEWorkflow;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ImplementationType;
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
        org.activiti.bpmn.model.Process process = ModelBuilder.createProcess(wf.getWorkflowName().trim());
        process.addFlowElement(ModelBuilder.createStartEvent());
        addListeners(process);
        parseWorkflowBlock(wf.getWorkflowStartBlock(), process, null);
        model.addProcess(process);
        //new BpmnAutoLayout(model).execute();
        String modelPath = WorkflowConstants.bpmnPath + wf.getWorkflowName().trim() + WorkflowConstants.bpmnExtension;
        ModelBuilder.saveModel(model, modelPath);
        wf.setBpmnPath(modelPath);
    }

    /*
     * Set the start and end execution listeners for the workflow
     * */
    private static void addListeners(Process process) {
        ActivitiListener startProcessListener = new ActivitiListener();
        startProcessListener.setImplementation(WorkflowConstants.processListenerImplementation);
        startProcessListener.setEvent(WorkflowConstants.startProcess);
        startProcessListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        ActivitiListener endProcessListener = new ActivitiListener();
        endProcessListener.setImplementation(WorkflowConstants.processListenerImplementation);
        endProcessListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        endProcessListener.setEvent(WorkflowConstants.endProcess);

        ArrayList<ActivitiListener> listeners = new ArrayList<ActivitiListener>();
        listeners.add(startProcessListener);
        listeners.add(endProcessListener);
        ModelBuilder.setListenersForProcess(process, listeners);

    }

    /*
     * Parse job engine blocks to bpmn blocks
     * */
    private static void parseWorkflowBlock(WorkflowBlock startBlock, Process process, WorkflowBlock previous) {

        if (previous != null) {
            process.addFlowElement(ModelBuilder.createSequenceFlow(previous.getJobEngineElementID(), startBlock.getJobEngineElementID(), previous.getCondition()));
        }
        if (startBlock.isProcessed()) return;
        startBlock.setProcessed(true);
        for (WorkflowBlock block : startBlock.getOutFlows().values()) {
            if (block instanceof EndBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createEndEvent());
            } else if (block instanceof ParallelGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createParallelGateway(block.getJobEngineElementID(), block.getName(),
                        block.generateBpmnInflows(), block.generateBpmnOutflows()));
            } /*else if (block instanceof SynchronizeBlock && !block.isProcessed()) {
				process.addFlowElement(ModelBuilder.createParallelGateway(block.getId(), block.getName(),
						block.generateBpmnInflows(), block.generateBpmnOutflows()));
			}*/ else if (block instanceof ScriptBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createScriptTask(block.getJobEngineElementID(), block.getName(),
                        ((ScriptBlock) block).getScript()));
            } else if (block instanceof ExclusiveGatewayBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createExclusiveGateway(block.getJobEngineElementID(), block.getName(),
                        ((ExclusiveGatewayBlock) block).isExclusive(), block.generateBpmnInflows(),
                        block.generateBpmnOutflows()));
            } else if (block instanceof DBWriteBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getName(),
                        WorkflowConstants.dbWriteTaskImplementation));
            } else if (block instanceof MailBlock && !block.isProcessed()) {
                process.addFlowElement(ModelBuilder.createServiceTask(block.getJobEngineElementID(), block.getName(),
                        WorkflowConstants.mailTaskImplementation));
            }

            parseWorkflowBlock(block, process, startBlock);
        }
    }

    //Test function
    public static void launchBuildTest(JEWorkflow wf) {
		/*StartBlock start = new StartBlock();
		start.setId("start");

		ScriptBlock script = new ScriptBlock();
		script.setScript("execution.setVariable(\"rowCount\", 50)");
		start.getOutFlows().add(script);
		script.getInflows().add(start);
		script.setId("script");
		ParallelGatewayBlock split = new ParallelGatewayBlock();
		split.setId("split");
		split.getInflows().add(script);
		script.getOutFlows().add(split);
		DBWriteBlock write = new DBWriteBlock();
		write.setId("write");
		write.getInflows().add(split);
		MailBlock mail = new MailBlock();
		mail.getInflows().add(split);
		mail.setId("mail");
		split.getOutFlows().add(write);
		split.getOutFlows().add(mail);
		SynchronizeBlock join = new SynchronizeBlock();
		join.setId("join");
		join.getInflows().add(write);
		join.getInflows().add(mail);
		write.getOutFlows().add(join);
		mail.getOutFlows().add(join);
		EndBlock end = new EndBlock();
		end.setId("end");
		end.getInflows().add(join);
		join.getOutFlows().add(end);*/

        createBpmnFromJEWorkflow( wf);
        HashMap<String, String> wfMap = new HashMap<String, String>();
        wfMap.put("key", wf.getJobEngineElementID());
        wfMap.put("path", "processes/testGenerated.bpmn");
        wfMap.put("projectId", wf.getJobEngineProjectID());
        try {
            Network.makeNetworkCallWithJsonBody(wfMap, "http://127.0.0.1:8081/addWorkflow");
        } catch (IOException e) {
            JELogger.info(JEToBpmnMapper.class, "Network Error");
        }
    }

    /*
     * Test JEToBpmn conversion
     * */
    public static void main(String[] args) {
		/*StartBlock start = new StartBlock();
		start.setJobEngineElementID("start");

		ScriptBlock script = new ScriptBlock();
		script.setScript("execution.setVariable(\"rowCount\", 50)");
		start.getOutFlows().add(script);
		script.getInflows().add(start);
		script.setJobEngineElementID("script");
		ParallelGatewayBlock split = new ParallelGatewayBlock();
		split.setJobEngineElementID("split");
		split.getInflows().add(script);
		script.getOutFlows().add(split);
		DBWriteBlock write = new DBWriteBlock();
		write.setJobEngineElementID("write");
		write.getInflows().add(split);
		MailBlock mail = new MailBlock();
		mail.getInflows().add(split);
		mail.setJobEngineElementID("mail");
		split.getOutFlows().add(write);
		split.getOutFlows().add(mail);
		SynchronizeBlock join = new SynchronizeBlock();
		join.setJobEngineElementID("join");
		join.getInflows().add(write);
		join.getInflows().add(mail);
		write.getOutFlows().add(join);
		mail.getOutFlows().add(join);
		EndBlock end = new EndBlock();
		end.setJobEngineElementID("end");
		end.getInflows().add(join);
		join.getOutFlows().add(end);*/

        //createBpmnFromJEWorkflow("generatedBpmn", start,"D:\\generatedBpmn.bpmn");
    }
}
