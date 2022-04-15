package io.je.rulebuilder.components;

public class BlockLinkModel {
	String blockId;
	int order;
	String variableName="";
	
	
	public BlockLinkModel(String blockId) {
		super();
		this.blockId = blockId;
	}
	public String getBlockId() {
		return blockId;
	}
	public void setBlockId(String block) {
		this.blockId = block;
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
	
	
	
}
