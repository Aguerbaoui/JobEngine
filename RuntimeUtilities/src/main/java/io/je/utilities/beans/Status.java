package io.je.utilities.beans;

public enum Status {
	ERROR("Error"),
	BUILDING("Building"),
	RUNNING("Running"),
	STOPPING("Stopping"),
	STOPPED("Stopped"),
	NOT_BUILT("To be Built"),
	RUNNING_NOT_UP_TO_DATE("Running-Not Updated"),
    TRIGGERED("Triggered"),
    NOT_TRIGGERED("Not Triggered");
	

    private final String name;       

    private Status(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
       return this.name;
    }


    public static Status fromString(String text) {
        for (Status b : Status.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
