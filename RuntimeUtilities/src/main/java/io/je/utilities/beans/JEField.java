package io.je.utilities.beans;

public class JEField {

    private UnifiedType type;

    private String visibility;

    private String comment;

    private String name;

    public JEField() {
    }

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

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
