package io.je.rulebuilder.components.blocks;

import io.je.utilities.runtimeobject.JEObject;



public abstract class Block extends JEObject{
	
	String ruleId;
	
	
	
	public Block(String jobEngineElementID, String jobEngineProjectID,String ruleId) {
		super(jobEngineElementID, jobEngineProjectID);
		this.ruleId=ruleId;
	}



	/* 
	 * returns a string that expresses this condition in the drools rule language.
	 */
	public abstract String getExpression();

}
