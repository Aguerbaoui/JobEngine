package io.je.utilities.beans;

import java.util.ArrayList;
import java.util.List;

public class JERuleMessage {
	
	 String type; //BlockMessage, RuleExecutionMessage, ErrorMessage
	 String executionTime;
	 List<JERuleBlockMessage> blocks;
	 List<String> instanceNames;
	 String message;
	 
	 
	 
	public JERuleMessage() {
		blocks = new ArrayList<JERuleBlockMessage>();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}
	public List<JERuleBlockMessage> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<JERuleBlockMessage> blocks) {
		this.blocks = blocks;
	}
	public List<String> getInstanceNames() {
		return instanceNames;
	}
	public void setInstanceNames(List<String> instanceNames) {
		this.instanceNames = instanceNames;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	 
	 
	 public void addBlockMessage(JERuleBlockMessage msg)
	 {
		 blocks.add(msg);
	 }
	 
	 
	
	

}
