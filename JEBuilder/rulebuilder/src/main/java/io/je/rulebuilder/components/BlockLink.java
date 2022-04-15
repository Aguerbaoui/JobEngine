package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.Block;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class BlockLink {
	Block block;
	int order;
	String variableName="";
	
	
	
	public String getReference()
	{
		return block.getReference(variableName);
	}
	
	
	
	
	public BlockLink(Block block) {
		super();
		this.block = block;
	}
	public Block getBlock() {
		return block;
	}
	public void setBlock(Block block) {
		this.block = block;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}




	public String getExpression() throws RuleBuildFailedException {
		return block.getExpression();
	}
	
	
	
}
