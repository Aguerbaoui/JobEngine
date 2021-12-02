package blocks.basic;

import blocks.WorkflowBlock;
import utils.network.AuthScheme;

import java.util.ArrayList;
import java.util.HashMap;

public class WebApiBlock extends WorkflowBlock {

    String url;

    String method;

    String body;

    HashMap<String, ArrayList<Object>> inputs;

    HashMap<String, String> outputs;

    AuthScheme authScheme;

    HashMap<String, String> authentication;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HashMap<String, String> getOutputs() {
        return outputs;
    }

    public void setOutputs(HashMap<String, String> outputs) {
        this.outputs = outputs;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HashMap<String, ArrayList<Object>> getInputs() {
        return inputs;
    }

    public void setInputs(HashMap<String, ArrayList<Object>> inputs) {
        this.inputs = inputs;
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
