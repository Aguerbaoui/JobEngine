package io.je.utilities.beans;

public class JERuleBlockMessage {
	
	String blockName;
	String blockValue;
	
	
	
	public JERuleBlockMessage(String blockName, String blockValue) {
		super();
		this.blockName = blockName;
		this.blockValue = blockValue;
	}
	public String getBlockName() {
		return blockName;
	}
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	public String getBlockValue() {
		return blockValue;
	}
	public void setBlockValue(String blockValue) {
		this.blockValue = blockValue;
	}
	
	
	

}
