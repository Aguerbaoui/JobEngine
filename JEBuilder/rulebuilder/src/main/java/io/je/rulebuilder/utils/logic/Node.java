package io.je.rulebuilder.utils.logic;

import java.util.ArrayList;
import java.util.List;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.blocks.GatewayBlock;
import io.je.rulebuilder.components.blocks.LogicalBlock;

public  class Node {
	LogicalBlock value;
	Node parent;
	List<Node> children;
	StringBuilder string = new StringBuilder();

	
	
	public String getString()
	{
		
		
		if(this.value instanceof ArithmeticBlock || this.children == null || this.children.isEmpty() )
		{
			string.append(this.getValue().getExpression());
			return string.toString();
		}
		if(this.value instanceof GatewayBlock)
		{
			int numberOfchildren= children.size();
			for (int i = 0; i<numberOfchildren;i++)
			{
				string.append(children.get(i).getString());
				if(i<numberOfchildren-1)
				{
					string.append(getValue().getExpression());
				}

				
			}
			return string.toString();
		}
		else
		{
			for (Node child : children)
			{
				string.append(child.getString());
				
			}
			string.append(getValue().getExpression());
			return string.toString();
			
		}
		
	}
	
	
	public Node(LogicalBlock value) {
		super();
		this.value = value;
	}



	public LogicalBlock getValue() {
		return value;
	}



	public void setValue(LogicalBlock value) {
		this.value = value;
	}



	public Node getParent() {
		return parent;
	}



	public void setParent(Node parent) {
		this.parent = parent;
	}



	public List<Node> getChildren() {
		return children;
	}



	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public Node getRoot() {
		 if(parent == null){
		  return this;
		 }
		 return parent.getRoot();
		}

	public void addChild(Node node) {
		
		this.children.add(node);
		node.setParent(this);
	}
	
	public void addChild(LogicalBlock block) {
		if(this.children==null)
		{
			this.children = new ArrayList<>();
		}
		Node node = new Node(block);
		this.children.add(node);
		node.setParent(this);
	}
	
	public void deleteNode() {
		if (parent != null) {
			int index = this.parent.getChildren().indexOf(this);
			this.parent.getChildren().remove(this);
			for (Node each : getChildren()) {
				each.setParent(this.parent);
			}
			this.parent.getChildren().addAll(index, this.getChildren());
		} else {
			deleteRootNode();
		}
		this.getChildren().clear();
	}

	public Node deleteRootNode() {
		if (parent != null) {
			throw new IllegalStateException("deleteRootNode not called on root");
		}
		Node newParent = null;
		if (!getChildren().isEmpty()) {
			newParent = getChildren().get(0);
			newParent.setParent(null);
			getChildren().remove(0);
			for (Node each : getChildren()) {
				each.setParent(newParent);
			}
			newParent.getChildren().addAll(getChildren());
		}
		this.getChildren().clear();
		return newParent;
	}

}
