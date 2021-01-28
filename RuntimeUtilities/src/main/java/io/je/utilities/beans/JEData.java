package io.je.utilities.beans;

import io.je.utilities.runtimeobject.JEObject;

public class JEData extends JEObject {

    private String data;

    private String topic;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public JEData() {
        super();
    }

    public JEData(String topic, String data) {
        super();
        this.data = data;
        this.topic = topic;
    }
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

	@Override
	public String toString() {
		return "JEData [data=" + data + ", topic=" + topic + "]";
	}
    
    
    
    
}
