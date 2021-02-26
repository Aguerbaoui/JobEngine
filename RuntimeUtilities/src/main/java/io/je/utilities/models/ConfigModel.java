package io.je.utilities.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection="JEConfiguration")
@JsonInclude(Include.NON_NULL)
public class ConfigModel {
	
	@Id
	 String identifier="ConfigJE";
	 String dataDefinitionURL;
	 String dataManagerURL;
	 String runtimeManagerURL;
	 String projectBuilderURL;
	 int subscriberPort;
	 int requestPort;

	public String getDataDefinitionURL() {
		return dataDefinitionURL;
	}
	public void setDataDefinitionURL(String dataDefinitionURL) {
		this.dataDefinitionURL = dataDefinitionURL;
	}
	public String getDataManagerURL() {
		return dataManagerURL;
	}
	public void setDataManagerURL(String dataManagerURL) {
		this.dataManagerURL = dataManagerURL;
	}
	public String getRuntimeManagerURL() {
		return runtimeManagerURL;
	}
	public void setRuntimeManagerURL(String runtimeManagerURL) {
		this.runtimeManagerURL = runtimeManagerURL;
	}
	public int getSubscriberPort() {
		return subscriberPort;
	}
	public void setSubscriberPort(int subscriberPort) {
		this.subscriberPort = subscriberPort;
	}
	public int getRequestPort() {
		return requestPort;
	}
	public void setRequestPort(int requestPort) {
		this.requestPort = requestPort;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getProjectBuilderURL() {
		return projectBuilderURL;
	}
	public void setProjectBuilderURL(String projectBuilderURL) {
		this.projectBuilderURL = projectBuilderURL;
	}
	
	

}
