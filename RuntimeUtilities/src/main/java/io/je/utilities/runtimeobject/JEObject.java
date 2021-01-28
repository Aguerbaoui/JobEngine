package io.je.utilities.runtimeobject;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection="JEObject")
public abstract class JEObject {


    @Id
    protected String jobEngineElementID;

    protected String jobEngineProjectID;

    protected LocalDateTime jeObjectLastUpdate;


    public JEObject(String jobEngineElementID, String jobEngineProjectID) {
        super();
        this.jobEngineElementID = jobEngineElementID;
        this.jobEngineProjectID = jobEngineProjectID;
        this.jeObjectLastUpdate = LocalDateTime.now();
        //TODO: add time config (format, timezone, etc ..)
        //set update time
    }


    //TODO: to be deleted. Only constructor with fields needs to be kept.
    public JEObject() {

    }

    public String getJobEngineElementID() {
        return jobEngineElementID;
    }


    public void setJobEngineElementID(String jobEngineElementID) {
        this.jobEngineElementID = jobEngineElementID;
    }

    public String getJobEngineProjectID() {
        return jobEngineProjectID;
    }

    public void setJobEngineProjectID(String jobEngineProjectID) {
        this.jobEngineProjectID = jobEngineProjectID;
    }

    public LocalDateTime getJeObjectLastUpdate() {
        return jeObjectLastUpdate;
    }

    public void setJeObjectLastUpdate(LocalDateTime jeObjectLastUpdate) {
        this.jeObjectLastUpdate = jeObjectLastUpdate;
    }

    // Overriding equals() to compare two JEObjects
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true   
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of JEObject or not */
        if (!(o instanceof JEObject)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members  
        JEObject jeObject = (JEObject) o;

        // if objects in different projects, return false 
       /* if (!jeObject.jobEngineProjectID.equals(this.jobEngineProjectID)) {
            return false;
        } */

        //if object are in the same project, and they share the same ID, then 
        //they're considered equal
        return jeObject.jobEngineElementID.equals(this.jobEngineElementID);
    }

    @Override
    public int hashCode() {
        return this.jobEngineElementID.hashCode();
    }


} 
	

