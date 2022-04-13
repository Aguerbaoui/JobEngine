package io.je.rulebuilder.components;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstanceGetterBlockOutputIds {	
	String attributeName;
	
	@JsonProperty("block")
	String blockId;
	int order;
	
	
	
	public InstanceGetterBlockOutputIds() {
		super();
		// TODO Auto-generated constructor stub
	}
	public InstanceGetterBlockOutputIds(String attributeName, String blockId, int order) {
		super();
		this.attributeName = attributeName;
		this.blockId = blockId;
		this.order = order;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getBlockId() {
		return blockId;
	}
	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	
}
