package io.je.rulebuilder.utils.logic;

import java.util.List;

import io.je.rulebuilder.components.blocks.LogicalBlock;

public abstract class Node {
	LogicalBlock value;
	Node parent;
	List<Node> children;
	
	
	
	
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
