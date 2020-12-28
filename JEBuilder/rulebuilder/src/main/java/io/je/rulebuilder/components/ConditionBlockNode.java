package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.GetterBlock;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.components.blocks.logic.JoinBlock;
import io.je.rulebuilder.components.blocks.LogicBlock;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.beliefsystem.defeasible.Join;

/*
 * This class represents a tree definition for the JobEngine Rule Condition
 * Each node has a ConditionBlock as value, a single parent and multiple children
 */
public class ConditionBlockNode {
	
	/*
	 * The node value. Each node holds a condition block
	 */
	ConditionBlock value;
	
	/*
	 * The node's parent ( equivalent to the block's output)
	 */
	ConditionBlockNode parent;
	
	/*
	 * list of all 
	 */
	List<ConditionBlockNode> children;


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
	 * returns a string expressing this condition in drools rule language
	 */
	public String getString(int ref, String expression) {

		StringBuilder drlExpression = new StringBuilder();

		// parse attribute getter block
		if (this.value instanceof AttributeGetterBlock) {
			AttributeGetterBlock block = (AttributeGetterBlock) this.value;

			// if parent does not require identifier
			if (ref == 1) {
				return block.getComparableExpression(expression);
			}
			// if parent requires identifier
			else if (ref == 2) {
				return block.getExpression(expression);
			}

		}

		// parse Arithmetic block
		if (this.value instanceof ArithmeticBlock) {
			ArithmeticBlock block = (ArithmeticBlock) this.value;

			// if single input

			drlExpression.append(children.get(0).getString(2, block.getOperationIdentifier()));
			drlExpression.append("\n");
			// if parent does not require identifier
			if (ref == 1) {
				drlExpression.append(block.getComparableExpression(expression));
			}
			// if parent requires identifier
			if (ref == 2) {
				drlExpression.append(block.getExpression("$randomVarName"));

			}

			return drlExpression.toString();

		}

		// parse comparison block
		if (this.value instanceof ComparisonBlock) {
			ComparisonBlock block = (ComparisonBlock) this.value;

			int numberOfChildren = this.children.size();
			// if single input
			if (numberOfChildren == 1) {
				// if input is getter block
				return children.get(0).getString(1, block.getExpression());

				// TODO: if input is another comparison block
			} else if (numberOfChildren == 2) {
				
				drlExpression.append(children.get(0).getString(2, block.getOperationIdentifier()));
				drlExpression.append("\n");
				drlExpression.append(children.get(1).getString(1, block.getExpression()));
				return drlExpression.toString();


				
			}
			else
			{
				//throw exception comparison block must have 1 or 2 inputs
			} 
		}
		
		// parse logic block
		if (this.value instanceof LogicBlock) {
			
			LogicBlock block = (LogicBlock) this.value;

			if ( block instanceof JoinBlock)
			{
				//drlExpression.append(children.get(0).getString(2, "joinattributename"));
			}
			else
			{
				drlExpression.append(" "+block.getExpression()+" (");
				for(ConditionBlockNode child : this.children)
				{
					drlExpression.append(child.getString(0, ""));
				}
			}

				
			
		}

		return drlExpression.toString();

	}

	/*
	 * Getters and setters
	 */

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

	// TODO: to be removed
	public void addChild(PersistableBlock block) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		ConditionBlockNode conditionBlockNode = new ConditionBlockNode(block);
		this.children.add(conditionBlockNode);
		conditionBlockNode.setParent(this);
	}

	// TODO: might be deleted
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
