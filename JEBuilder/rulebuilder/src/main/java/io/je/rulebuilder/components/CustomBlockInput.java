package io.je.rulebuilder.components;

import org.springframework.data.annotation.Transient;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.getter.InstanceGetterBlock;

public class CustomBlockInput {

	String blockId;
	String blockReference;
	
	@Transient
	InstanceGetterBlock value;
	
	public String getBlockId() {
		return blockId;
	}
	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}
	public String getBlockReference() {
		return blockReference;
	}
	public void setBlockReference(String blockReference) {
		this.blockReference = blockReference;
	}
	public InstanceGetterBlock getValue() {
		return value;
	}
	public void setValue(InstanceGetterBlock input) {
		this.value = input;
	}
	
	
}
