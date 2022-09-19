package io.je.serviceTasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.network.AuthScheme;
import utils.network.BodyType;
import utils.network.HttpMethod;

import java.util.HashMap;

public class WebApiTask extends ActivitiTask {

    private HttpMethod httpMethod;
    private BodyType bodyType;
    private String stringBody;
    private HashMap<String, String> body;
    private String responseClass;
    private boolean hasBody;
    private AuthScheme authScheme = AuthScheme.NONE;
    private HashMap<String, String> authentication;
    private String url;

    public WebApiTask() {
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public boolean hasBody() {
        return hasBody;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public String getBody() throws JsonProcessingException {
        if (stringBody != null) {
            return stringBody;
        }
        return new ObjectMapper().writeValueAsString(body);
    }

    public void setBody(HashMap<String, String> body) {
        if (body != null) {
            this.body = body;
            this.hasBody = true;
        }
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

    public String getStringBody() {
        return stringBody;
    }

    public void setStringBody(String stringBody) {
        this.stringBody = stringBody;
    }

    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }

    public AuthScheme getAuthScheme() {
        return authScheme;
    }

    public void setAuthScheme(AuthScheme authScheme) {
        this.authScheme = authScheme;
    }

    public HashMap<String, String> getAuthentication() {
        return authentication;
    }

    public void setAuthentication(HashMap<String, String> authentication) {
        this.authentication = authentication;
    }
}
