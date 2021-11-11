package io.je.utilities.beans;

public enum Status {
	ERROR("Error"),
	BUILDING("Building"),
	RUNNING("Running"),
	STOPPING("Stopping"),
	STOPPED("Stopped"),
	NOT_BUILT("Not Built"),
	RUNNING_NOT_UP_TO_DATE("Running/Not Updated"),
    TRIGGERED("Triggered"),
    NOT_TRIGGERED("Not triggered");
	

    private final String name;       

    private Status(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
	
	

}
