package io.je.rulebuilder.components.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;

public class GenericBlockSet {

	String classId ;
	private List<Block> blocks;
	boolean allBlocksAreGetters =true;
	boolean allBlocksAreSetters = true;

	

	
	public GenericBlockSet(String classId) {
		super();
		blocks = new ArrayList<Block>();
		this.classId = classId;
	}

	public boolean contains(Block block) {
		return blocks.contains(block);
	}

	public void add(Block block) {
		if(block instanceof AttributeGetterBlock)
		{
			allBlocksAreSetters = false;
		}else
		{
			allBlocksAreGetters =false;
		}
		blocks.add(block);
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public List<Block> getValue() {
		// TODO Auto-generated method stub
		return blocks;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}

	public boolean isAllBlocksAreGetters() {
		return allBlocksAreGetters;
	}

	public void setAllBlocksAreGetters(boolean allBlocksAreGetters) {
		this.allBlocksAreGetters = allBlocksAreGetters;
	}

	public boolean isAllBlocksAreSetters() {
		return allBlocksAreSetters;
	}

	public void setAllBlocksAreSetters(boolean allBlocksAreSetters) {
		this.allBlocksAreSetters = allBlocksAreSetters;
	}

	public Optional<Block> getIdentifier() {
		
		return blocks.stream().filter(bl->bl instanceof AttributeGetterBlock).findFirst();
	}
	
public Optional<Block> getIdentifier(String id) {
		
		return blocks.stream().filter(bl->(bl instanceof AttributeGetterBlock) && bl.blockName.equals(id)).findFirst();
	}
	
}
