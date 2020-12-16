package io.je;

public class JEProcess {

    public String key;

    public String name;

    public String bpmnPath;

    public JEProcess(String key, String name, String bpmnPath) {
        super();
        this.key = key;
        this.name = name;
        this.bpmnPath = bpmnPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBpmnPath() {
        return bpmnPath;
    }

    public void setBpmnPath(String bpmnPath) {
        this.bpmnPath = bpmnPath;
    }

}
