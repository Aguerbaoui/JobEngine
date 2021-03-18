package io.je.serviceTasks;

import io.je.utilities.apis.BodyType;
import io.je.utilities.apis.HttpMethod;

import java.util.HashMap;

public class WebApiTask extends  ActivitiTask{

    public WebApiTask() {}
    private HttpMethod httpMethod;

    private BodyType bodyType;

    private HashMap<String, String> body;

    private String responseClass;

    private boolean hasBody;

    private String url;

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public boolean hasBody() {
        return hasBody;
    }

    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public HashMap<String, String> getBody() {
        return body;
    }

    public void setBody(HashMap<String, String> body) {
        this.body = body;
    }

    public String getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(String responseClass) {
        this.responseClass = responseClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
