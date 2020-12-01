package builder;

import java.util.ArrayList;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;

import blocks.WorkflowBlock;
import blocks.basic.DBWriteBlock;
import blocks.basic.EndBlock;
import blocks.basic.MailBlock;
import blocks.basic.ScriptBlock;
import blocks.basic.StartBlock;
import blocks.control.OrJoinBlock;
import blocks.control.SplitBlock;
import blocks.control.SynchronizeBlock;
import io.je.utilities.constants.WorkflowConstants;

public class JEToBpmnMapper {

	/*
	 * private constructor
	 * */
	private JEToBpmnMapper() {
	}

	/*
	 * Generate and save bpmn workflow from JE workflow
	 * */
	public static BpmnModel createBpmnFromJEWorkflow(String processKey, StartBlock startBlock, String modelPath) {
		BpmnModel model = ModelBuilder.createNewBPMNModel();

		org.activiti.bpmn.model.Process process = ModelBuilder.createProcess(processKey);
		process.addFlowElement(ModelBuilder.createStartEvent());
		addListeners(process);
		parseWorkflowBlock(startBlock, process, null);
		model.addProcess(process);
		//new BpmnAutoLayout(model).execute();
		ModelBuilder.saveModel(model, "test");
		return model;
	}

	/*
	 * Set the start and end execution listeners for the workflow
	 * */
	private static void addListeners(Process process) {
		ActivitiListener startProcessListener = new ActivitiListener();
		startProcessListener.setImplementation(WorkflowConstants.processListenerImplementation);
		startProcessListener.setEvent(WorkflowConstants.startProcess);

		ActivitiListener endProcessListener = new ActivitiListener();
		endProcessListener.setImplementation(WorkflowConstants.processListenerImplementation);
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
		
		if(startBlock.isProcessed()) return;
		if (previous != null) {
			process.addFlowElement(ModelBuilder.createSequenceFlow(previous.getId(), startBlock.getId(), ""));
		}
		for (WorkflowBlock block : startBlock.getOutFlows()) {
			if (block instanceof EndBlock) {
				process.addFlowElement(ModelBuilder.createEndEvent());
				return;
			}

			else if (block instanceof SplitBlock) {
				process.addFlowElement(ModelBuilder.createParallelGateway(block.getId(), block.getName(),
						block.generateBpmnInflows(), block.generateBpmnOutflows()));
			} else if (block instanceof SynchronizeBlock) {
				process.addFlowElement(ModelBuilder.createParallelGateway(block.getId(), block.getName(),
						block.generateBpmnInflows(), block.generateBpmnOutflows()));
			} else if (block instanceof ScriptBlock) {
				process.addFlowElement(ModelBuilder.createScriptTask(block.getId(), block.getName(),
						((ScriptBlock) block).getScript()));
			} else if (block instanceof OrJoinBlock) {
				process.addFlowElement(ModelBuilder.createExclusiveGateway(block.getId(), block.getName(),
						((OrJoinBlock) block).isExclusive(), block.generateBpmnInflows(),
						block.generateBpmnOutflows()));
			} else if (block instanceof DBWriteBlock) {
				process.addFlowElement(ModelBuilder.createServiceTask(block.getId(), block.getName(),
						WorkflowConstants.dbWriteTaskImplementation));
			} else if (block instanceof MailBlock) {
				process.addFlowElement(ModelBuilder.createServiceTask(block.getId(), block.getName(),
						WorkflowConstants.mailTaskImplementation));
			}
			startBlock.setProcessed(true);
			parseWorkflowBlock(block, process, startBlock);
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
		SplitBlock split = new SplitBlock();
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
		write.getOutFlows().add(join);
		EndBlock end = new EndBlock();
		end.setId("id");
		end.getInflows().add(join);
		join.getOutFlows().add(end);

		createBpmnFromJEWorkflow("test", start,"D:\\test.bpmn");
	}
}
