package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.Block;

public class CustomBlockLink {	
	String attributeName;
	Block block;
	int order;
	
	
	
	public CustomBlockLink() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CustomBlockLink(String attributeName, Block block, int order) {
		super();
		this.attributeName = attributeName;
		this.block = block;
		this.order = order;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
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
	
	
}
