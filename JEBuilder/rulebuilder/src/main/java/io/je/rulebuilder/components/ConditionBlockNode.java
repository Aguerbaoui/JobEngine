package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.rulebuilder.components.blocks.LogicBlock;


import java.util.ArrayList;
import java.util.List;


/*
 * This class represents a tree definition for the JobEngine Rule Condition
 * Each node has a ConditionBlock as value, a single parent and multiple children
 */
public class ConditionBlockNode {
	ConditionBlock value;
    ConditionBlockNode parent;
    List<ConditionBlockNode> children;
    
    //the translation of the condition into Drools Rule Language (when) 
    StringBuilder drlExpression = new StringBuilder();


    /*
     * Constructor
     */
    public ConditionBlockNode(ConditionBlock value) {
        super();
        this.value = value;
    }
    
    
    /*
     * returns the root node 
     */
    
    public ConditionBlockNode getRoot() {
        if (parent == null) {
            return this;
        }
        return parent.getRoot();
    }
   
    /*
     * Getters and setters
     */

    public String getString() {


        if (this.children == null || this.children.isEmpty()) {
            drlExpression.append(this.getValue().getExpression());
            return drlExpression.toString();
        }
        if (this.value instanceof ArithmeticBlock) {

        }
        if (this.value instanceof LogicBlock) {
            int numberOfchildren = children.size();
            for (int i = 0; i < numberOfchildren; i++) {
                drlExpression.append(children.get(i).getString());
                if (i < numberOfchildren - 1) {
                    drlExpression.append(getValue().getExpression());
                }


            }
            return drlExpression.toString();
        } else {
            drlExpression.append("(");
            drlExpression.append(" " + getValue().getExpression() + " ");
            for (ConditionBlockNode child : children) {
                drlExpression.append(child.getString());

            }
            drlExpression.append(")");
            return drlExpression.toString();

        }

    }

    public ConditionBlock getValue() {
        return value;
    }


    public void setValue(PersistableBlock value) {
        this.value = value;
    }


    public ConditionBlockNode getParent() {
        return parent;
    }


    public void setParent(ConditionBlockNode parent) {
        this.parent = parent;
    }


    public List<ConditionBlockNode> getChildren() {
        return children;
    }


    public void setChildren(List<ConditionBlockNode> children) {
        this.children = children;
    }

    
    


    public void addChild(ConditionBlockNode conditionBlockNode) {

  	  if (this.children == null) {
            this.children = new ArrayList<>();
        }
      this.children.add(conditionBlockNode);
      conditionBlockNode.setParent(this);
  }
    
    //TODO: to be removed
    public void addChild(PersistableBlock block) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        ConditionBlockNode conditionBlockNode = new ConditionBlockNode(block);
        this.children.add(conditionBlockNode);
        conditionBlockNode.setParent(this);
    }
  
    //TODO: might be deleted
    public void deleteNode() {
        if (parent != null) {
            int index = this.parent.getChildren().indexOf(this);
            this.parent.getChildren().remove(this);
            for (ConditionBlockNode each : getChildren()) {
                each.setParent(this.parent);
            }
            this.parent.getChildren().addAll(index, this.getChildren());
        } else {
            deleteRootNode();
        }
        this.getChildren().clear();
    }

    public ConditionBlockNode deleteRootNode() {
        if (parent != null) {
            throw new IllegalStateException("deleteRootNode not called on root");
        }
        ConditionBlockNode newParent = null;
        if (!getChildren().isEmpty()) {
            newParent = getChildren().get(0);
            newParent.setParent(null);
            getChildren().remove(0);
            for (ConditionBlockNode each : getChildren()) {
                each.setParent(newParent);
            }
            newParent.getChildren().addAll(getChildren());
        }
        this.getChildren().clear();
        return newParent;
    }

}
