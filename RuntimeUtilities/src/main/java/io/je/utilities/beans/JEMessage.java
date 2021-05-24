package io.je.utilities.beans;

import java.util.ArrayList;
import java.util.List;

public class JEMessage {
	
	 String type; //BlockMessage, RuleExecutionMessage, ErrorMessage, Event
	 String executionTime;
	 List<JEBlockMessage> blocks;
	 List<String> instanceNames;
	 String message;
	 
	 
	 
	public JEMessage() {
		blocks = new ArrayList<JEBlockMessage>();
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
	public List<JEBlockMessage> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<JEBlockMessage> blocks) {
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
	 
	 
	 public void addBlockMessage(JEBlockMessage msg)
	 {
		 blocks.add(msg);
	 }
	 
	 
	
	

}
