package io.je.classbuilder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.utilities.beans.UnifiedType;

public class FieldModel {

    @JsonProperty(ClassModelAttributeMapping.FIELDNAME)
    private String name;

    @JsonProperty(ClassModelAttributeMapping.FIELDTYPE)
    private UnifiedType type;

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

    public UnifiedType getType() {
        return type;
    }

    public void setType(UnifiedType type) {
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
