package io.je.utilities.beans;


public class RunnerRequestObject {
    RunnerRequestEnum request;
    Object requestBody;


    public RunnerRequestObject() {

    }

    public RunnerRequestObject(RunnerRequestEnum request) {
        super();
        this.request = request;
    }

    public RunnerRequestEnum getRequest() {
        return request;
    }

    public void setRequest(RunnerRequestEnum request) {
        this.request = request;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }


}
