package io.je.utilities.beans;

import io.je.utilities.ruleutils.OperationStatusDetails;

import java.util.List;

public class JECustomResponse extends JEResponse {

    List<OperationStatusDetails> details;


    public JECustomResponse(int code, String message, List<OperationStatusDetails> details) {
        super(code, message);
        this.details = details;
    }


    public List<OperationStatusDetails> getDetails() {
        return details;
    }


    public void setDetails(List<OperationStatusDetails> details) {
        this.details = details;
    }


}
