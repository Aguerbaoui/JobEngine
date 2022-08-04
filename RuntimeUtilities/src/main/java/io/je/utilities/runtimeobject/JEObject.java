package io.je.utilities.runtimeobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "JEObject")
public abstract class JEObject {


    @Id
    protected String jobEngineElementID;

    protected String jobEngineProjectID;

    protected String jobEngineElementName;

    protected String jobEngineProjectName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    protected Instant jeObjectLastUpdate;

    protected Instant jeObjectCreationDate;

    protected String jeObjectCreatedBy;

    protected String jeObjectModifiedBy;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    protected String className;

    public JEObject(String jobEngineElementID, String jobEngineProjectID, String jobEngineElementName) {
        super();
        this.jobEngineElementID = jobEngineElementID;
        this.jobEngineProjectID = jobEngineProjectID;
        this.jobEngineElementName = jobEngineElementName;
        this.jeObjectLastUpdate = Instant.now();
        this.jeObjectCreationDate = Instant.now();
        //TODO: add time config (format, timezone, etc ..)
        //set update time
    }

    public JEObject(String jobEngineElementID, String jobEngineProjectID, String jobEngineElementName, String jobEngineProjectName) {
        super();
        this.jobEngineElementID = jobEngineElementID;
        this.jobEngineProjectID = jobEngineProjectID;
        this.jobEngineElementName = jobEngineElementName;
        this.jobEngineProjectName = jobEngineProjectName;
        this.jeObjectLastUpdate = Instant.now();
        this.jeObjectCreationDate = Instant.now();
        //TODO: add time config (format, timezone, etc ..)
        //set update time
    }


    //TODO: to be deleted. Only constructor with fields needs to be kept.
    protected JEObject() {
        this.jeObjectLastUpdate = Instant.now();
        this.jeObjectCreationDate = Instant.now();
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

    public Instant getJeObjectLastUpdate() {
        return jeObjectLastUpdate;
    }

    public void setJeObjectLastUpdate(Instant jeObjectLastUpdate) {
        /*      if (jeObjectLastUpdate != null)*/
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


    public Instant getJeObjectCreationDate() {
        return jeObjectCreationDate;
    }


    public void setJeObjectCreationDate(Instant jeObjectCreationDate) {
        this.jeObjectCreationDate = jeObjectCreationDate;
    }


    public String getJobEngineElementName() {
        return jobEngineElementName;
    }


    public void setJobEngineElementName(String jobEngineElementName) {
        this.jobEngineElementName = jobEngineElementName;
    }


    public String getJeObjectCreatedBy() {
        return jeObjectCreatedBy;
    }


    public void setJeObjectCreatedBy(String jeObjectCreatedBy) {
        if (jeObjectCreatedBy != null) {
            this.jeObjectCreatedBy = jeObjectCreatedBy;
        }
    }


    public String getJeObjectModifiedBy() {
        return jeObjectModifiedBy;
    }


    public void setJeObjectModifiedBy(String jeObjectModifiedBy) {
        if (jeObjectModifiedBy != null) {
            this.jeObjectModifiedBy = jeObjectModifiedBy;
        }
    }


    public String getJobEngineProjectName() {
        return jobEngineProjectName;
    }


    public void setJobEngineProjectName(String jobEngineProjectName) {
        this.jobEngineProjectName = jobEngineProjectName;
    }


} 
	

