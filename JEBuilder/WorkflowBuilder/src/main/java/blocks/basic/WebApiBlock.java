package blocks.basic;

import blocks.WorkflowBlock;

import java.util.ArrayList;
import java.util.HashMap;

public class WebApiBlock extends WorkflowBlock {
    String description;

    String url;

    String method;

    HashMap<String, ArrayList<Object>> inputs;

    HashMap<String, String> outputs;

    public HashMap<String, String> getOutputs() {
        return outputs;
    }

    public void setOutputs(HashMap<String, String> outputs) {
        this.outputs = outputs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}
