package builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.Process;

import blocks.WorkflowBlock;
import blocks.basic.DBWriteBlock;
import blocks.basic.EndBlock;
import blocks.basic.MailBlock;
import blocks.basic.ScriptBlock;
import blocks.basic.StartBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.control.SynchronizeBlock;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;
import models.JEWorkflow;

public class JEToBpmnMapper {

	/*
	 * private constructor
	 * */
	private JEToBpmnMapper() {
	}

	/*
	 * Generate and save bpmn workflow from JE workflow
	 * */
	public static BpmnModel createBpmnFromJEWorkflow(String processKey, JEWorkflow wf, String modelPath) {
		BpmnModel model = ModelBuilder.createNewBPMNModel();

		org.activiti.bpmn.model.Process process = ModelBuilder.createProcess(processKey);
		process.addFlowElement(ModelBuilder.createStartEvent());
		addListeners(process);
		parseWorkflowBlock(wf.getWorkflowStartBlock(), process, null);
		model.addProcess(process);
		//new BpmnAutoLayout(model).execute();
		ModelBuilder.saveModel(model, modelPath);
		wf.setBpmnPath(modelPath);
		return model;
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
			process.addFlowElement(ModelBuilder.createSequenceFlow(previous.getId(), startBlock.getId(), previous.getCondition()));
		}
		if(startBlock.isProcessed()) return;
	 	startBlock.setProcessed(true);
		for (WorkflowBlock block : startBlock.getOutFlows()) {
			if (block instanceof EndBlock && !block.isProcessed()) {
				process.addFlowElement(ModelBuilder.createEndEvent());
			}

			else if (block instanceof ParallelGatewayBlock && !block.isProcessed()) {
				process.addFlowElement(ModelBuilder.createParallelGateway(block.getId(), block.getName(),
						block.generateBpmnInflows(), block.generateBpmnOutflows()));
			} /*else if (block instanceof SynchronizeBlock && !block.isProcessed()) {
				process.addFlowElement(ModelBuilder.createParallelGateway(block.getId(), block.getName(),
						block.generateBpmnInflows(), block.generateBpmnOutflows()));
			}*/ else if (block instanceof ScriptBlock && !block.isProcessed()) {
				process.addFlowElement(ModelBuilder.createScriptTask(block.getId(), block.getName(),
						((ScriptBlock) block).getScript()));
			} else if (block instanceof ExclusiveGatewayBlock && !block.isProcessed()) {
				process.addFlowElement(ModelBuilder.createExclusiveGateway(block.getId(), block.getName(),
						((ExclusiveGatewayBlock) block).isExclusive(), block.generateBpmnInflows(),
						block.generateBpmnOutflows()));
			} else if (block instanceof DBWriteBlock && !block.isProcessed()) {
				process.addFlowElement(ModelBuilder.createServiceTask(block.getId(), block.getName(),
						WorkflowConstants.dbWriteTaskImplementation));
			} else if (block instanceof MailBlock && !block.isProcessed()) {
				process.addFlowElement(ModelBuilder.createServiceTask(block.getId(), block.getName(),
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

		createBpmnFromJEWorkflow(wf.getId(), wf,"D:\\Job engine\\JERunner\\WorkflowEngine\\src\\main\\resources\\processes\\testGenerated.bpmn");
		HashMap<String, String> wfMap = new HashMap<String, String>();
		wfMap.put("key",wf.getId());
		wfMap.put("path", "processes/testGenerated.bpmn");
		wfMap.put("projectId", wf.getProjectId());
		try {
			Network.makeNetworkCallWithJsonBody(wfMap, "http://127.0.0.1:8081/addWorkflow");
		} catch (IOException e) {
			JELogger.info("Network Error");
		}
	}

	/*
	 * Test JEToBpmn conversion
	 * */
	public static void main(String[] args) {
		StartBlock start = new StartBlock();
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
		join.getOutFlows().add(end);

		//createBpmnFromJEWorkflow("generatedBpmn", start,"D:\\generatedBpmn.bpmn");
	}
}
