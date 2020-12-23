package io.je.rulebuilder.components.blocks;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.utilities.runtimeobject.JEObject;

/*
 * Job Engine block
 */
public abstract class Block extends JEObject {

   protected String ruleId;    
   protected List<String> inputBlocks = new ArrayList<>();
   protected List<String> outputBlocks = new ArrayList<>();

    
    


	public Block(String jobEngineElementID, String jobEngineProjectID, String ruleId,
			List<String> inputBlocks, List<String> outputBlocks) {
		super(jobEngineElementID, jobEngineProjectID);
		this.ruleId = ruleId;
		this.inputBlocks = inputBlocks;
		this.outputBlocks = outputBlocks;
	}




	public String getRuleId() {
		return ruleId;
	}




	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}









	public List<String> getOutputBlocks() {
		return outputBlocks;
	}




	public void setOutputBlocks(List<String> outputBlocks) {
		this.outputBlocks = outputBlocks;
	}




	public List<String> getInputBlocks() {
		return inputBlocks;
	}




	public void setInputBlocks(List<String> inputBlocks) {
		this.inputBlocks = inputBlocks;
	}




	/*
     * returns a string that describes this block in the drools rule language.
     */
    public abstract String getExpression();

}
