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
    public int hashCode() {
        return this.jobEngineElementID.hashCode();
    }


} 
	

