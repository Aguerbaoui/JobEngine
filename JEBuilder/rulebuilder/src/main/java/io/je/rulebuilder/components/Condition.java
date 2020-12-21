package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.BlockInventory;
import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.rulebuilder.components.blocks.LogicBlock;


import java.util.ArrayList;
import java.util.List;

public class Condition {
	ConditionBlock value;
    Condition parent;
    List<Condition> children;
    StringBuilder string = new StringBuilder();


    public Condition(ConditionBlock value) {
        super();
        this.value = value;
    }
    
    public Condition createCondition(ConditionBlock block)
    {
    	Condition condition = new Condition(block);
    	
    	if( block.getInputBlocks().isEmpty()) 
    	{
    		return condition;
    	}
    	
    	if(block instanceof LogicBlock)
    	{
    		for(String inputBlockId : block.getInputBlocks())
    		{
    			ConditionBlock inputBlock = (ConditionBlock) BlockInventory.getBlock(inputBlockId);
    			if (inputBlock!=null)
    			{
    				condition.addChild(createCondition(inputBlock));
    				
    			}
    		}    	
    	}
    	else
    	{
    		
    	
    	}
		return condition;

    }

    public String getString() {


        if (this.children == null || this.children.isEmpty()) {
            string.append(this.getValue().getExpression());
            return string.toString();
        }
        if (this.value instanceof ArithmeticBlock) {

        }
        if (this.value instanceof LogicBlock) {
            int numberOfchildren = children.size();
            for (int i = 0; i < numberOfchildren; i++) {
                string.append(children.get(i).getString());
                if (i < numberOfchildren - 1) {
                    string.append(getValue().getExpression());
                }


            }
            return string.toString();
        } else {
            string.append("(");
            string.append(" " + getValue().getExpression() + " ");
            for (Condition child : children) {
                string.append(child.getString());

            }
            string.append(")");
            return string.toString();

        }

    }

    public ConditionBlock getValue() {
        return value;
    }


    public void setValue(PersistableBlock value) {
        this.value = value;
    }


    public Condition getParent() {
        return parent;
    }


    public void setParent(Condition parent) {
        this.parent = parent;
    }


    public List<Condition> getChildren() {
        return children;
    }


    public void setChildren(List<Condition> children) {
        this.children = children;
    }

    public Condition getRoot() {
        if (parent == null) {
            return this;
        }
        return parent.getRoot();
    }

    public void addChild(Condition condition) {

        this.children.add(condition);
        condition.setParent(this);
    }

    public void addChild(PersistableBlock block) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        Condition condition = new Condition(block);
        this.children.add(condition);
        condition.setParent(this);
    }

    public void deleteNode() {
        if (parent != null) {
            int index = this.parent.getChildren().indexOf(this);
            this.parent.getChildren().remove(this);
            for (Condition each : getChildren()) {
                each.setParent(this.parent);
            }
            this.parent.getChildren().addAll(index, this.getChildren());
        } else {
            deleteRootNode();
        }
        this.getChildren().clear();
    }

    public Condition deleteRootNode() {
        if (parent != null) {
            throw new IllegalStateException("deleteRootNode not called on root");
        }
        Condition newParent = null;
        if (!getChildren().isEmpty()) {
            newParent = getChildren().get(0);
            newParent.setParent(null);
            getChildren().remove(0);
            for (Condition each : getChildren()) {
                each.setParent(newParent);
            }
            newParent.getChildren().addAll(getChildren());
        }
        this.getChildren().clear();
        return newParent;
    }

}
