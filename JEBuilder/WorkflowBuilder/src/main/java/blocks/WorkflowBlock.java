package blocks;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.SequenceFlow;

import builder.ModelBuilder;
import io.je.utilities.runtimeobject.JEObject;

/*
 * Model class for a workflow block
 * */
public class WorkflowBlock extends JEObject{

	/*
	 * Incoming flows
	 * */
	private ArrayList<WorkflowBlock> inflows;
	
	/*
	 * Outgoing flows
	 * */
	private ArrayList<WorkflowBlock> outFlows;

	/*
	 * Block Name
	 * */
	private String name;
	
	/*
	 * Block processing state ( true if parsed in the tree )
	 * */
	private boolean processed;
	
	/*
	 * Constructor
	 * */
	public WorkflowBlock() {
		inflows = new ArrayList<WorkflowBlock>();
		outFlows = new ArrayList<WorkflowBlock>();
		processed = false;
	}
	
	/*
	 * Returns incoming flows
	 * */
	public ArrayList<WorkflowBlock> getInflows() {
		return inflows;
	}

	/*
	 * Sets incoming flows
	 * */
	public void setInflows(ArrayList<WorkflowBlock> inflows) {
		this.inflows = inflows;
	}

	/*
	 * Returns outgoing flows
	 * */
	public ArrayList<WorkflowBlock> getOutFlows() {
		return outFlows;
	}

	/*
	 * Set outgoing flows
	 * */
	public void setOutFlows(ArrayList<WorkflowBlock> outFlows) {
		this.outFlows = outFlows;
	}

	/*
	 * Returns block name
	 * */
	public String getName() {
		return name;
	}

	/*
	 * Sets block name
	 * */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * Returns generated bmpn incoming flows
	 * */
	public List<SequenceFlow> generateBpmnInflows() {
		List<SequenceFlow> l = new ArrayList<SequenceFlow>();
		for(WorkflowBlock block: inflows) {
			l.add(ModelBuilder.createSequenceFlow(block.id, this.id, ""));
		}
		return l;
	}
	
	/*
	 * Returns generated bmpn outgoing flows
	 * */
	public List<SequenceFlow> generateBpmnOutflows() {
		List<SequenceFlow> l = new ArrayList<SequenceFlow>();
		for(WorkflowBlock block: outFlows) {
			l.add(ModelBuilder.createSequenceFlow(this.id, block.id, ""));
		}
		return l;
	}

	/*
	 * Returns if block is processed in the tree 
	 * */
	public boolean isProcessed() {
		return processed;
	}

	/*
	 * Sets block processed state
	 * */
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}


	
}
