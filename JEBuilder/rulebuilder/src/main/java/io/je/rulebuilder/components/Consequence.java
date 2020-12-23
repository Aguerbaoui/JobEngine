package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.ExecutionBlock;

public class Consequence {
    ExecutionBlock value;
    
    
    

    public Consequence(ExecutionBlock value) {
		super();
		this.value = value;
	}




	public String getExpression() {
        return "JELogger.info(\"test\" )";
    }

}
