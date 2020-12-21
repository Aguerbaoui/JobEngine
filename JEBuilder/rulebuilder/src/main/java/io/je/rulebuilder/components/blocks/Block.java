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

    String ruleId;    
    int operationId;
    List<String> inputBlocks = new ArrayList<>();

    
    


	public String getRuleId() {
		return ruleId;
	}




	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}




	public int getOperationId() {
		return operationId;
	}




	public void setOperationId(int operationId) {
		this.operationId = operationId;
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
