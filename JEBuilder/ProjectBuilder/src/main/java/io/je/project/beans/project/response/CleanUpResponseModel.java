package io.je.project.beans.project.response;


import lombok.Data;

/*
 * Model class for CleanUp request response
 * */
@Data
public class CleanUpResponseModel {

    public String componentName;
    public boolean result;
    public String strError;


}
