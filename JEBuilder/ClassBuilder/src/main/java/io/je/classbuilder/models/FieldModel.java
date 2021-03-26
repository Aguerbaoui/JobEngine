package io.je.classbuilder.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FieldModel {

    @JsonProperty(ClassModelAttributeMapping.FIELDNAME)
	private String name;
    
    @JsonProperty(ClassModelAttributeMapping.FIELDTYPE)
	private String type;
    
    @JsonProperty(ClassModelAttributeMapping.FIELDCOMMENT)
	private String comment;
    
    
    @JsonProperty(ClassModelAttributeMapping.FIELDVISIBILITY)
    private String fieldVisibility;
    
	
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

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getFieldVisibility() {
		return fieldVisibility;
	}
	public void setFieldVisibility(String fieldVisibility) {
		this.fieldVisibility = fieldVisibility;
	}
	
	 
	
	

}
