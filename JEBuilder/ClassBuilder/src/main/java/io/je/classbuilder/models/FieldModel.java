package io.je.classbuilder.models;

public class FieldModel {
	
	private String id;
	private String name;
	private String type;
	private boolean hasGetter;
	private boolean hasSetter;
	private String comment;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean getHasGetter() {
		return hasGetter;
	}
	public void setHasGetter(boolean hasGetter) {
		this.hasGetter = hasGetter;
	}
	public boolean getHasSetter() {
		return hasSetter;
	}
	public void setHasSetter(boolean hasSetter) {
		this.hasSetter = hasSetter;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	@Override
	public String toString() {
		return "FieldModel [id=" + id + ", name=" + name + ", type=" + type + ", hasGetter=" + hasGetter
				+ ", hasSetter=" + hasSetter + ", comment=" + comment + "]";
	}
	
	

}
