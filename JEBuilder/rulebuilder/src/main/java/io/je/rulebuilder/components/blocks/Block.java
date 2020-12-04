package io.je.rulebuilder.components.blocks;

import io.je.utilities.runtimeobject.JEObject;

public abstract class Block extends JEObject{
	
	/* 
	 * returns a string that expresses this condition in the drools rule language.
	 */
	public abstract String getExpression();

}
