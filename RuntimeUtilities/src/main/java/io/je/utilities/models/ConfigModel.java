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
	 String emailApiUrl;
	 int subscriberPort;
	 int requestPort;
	 String droolsDateFormat;
	 String dataModelDateFormat;
	 String loggingSystemURL;
	 int loggingSystemZmqPublishPort;

	public String getEmailApiUrl() {
		return emailApiUrl;
	}

	public void setEmailApiUrl(String emailApiUrl) {
		this.emailApiUrl = emailApiUrl;
	}

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
	public String getDroolsDateFormat() {
		return droolsDateFormat;
	}
	public void setDroolsDateFormat(String droolsDateFormat) {
		this.droolsDateFormat = droolsDateFormat;
	}

	

	
	public String getLoggingSystemURL() {
		return loggingSystemURL;
	}

	public void setLoggingSystemURL(String loggingSystemURL) {
		this.loggingSystemURL = loggingSystemURL;
	}

	public int getLoggingSystemZmqPublishPort() {
		return loggingSystemZmqPublishPort;
	}

	public void setLoggingSystemZmqPublishPort(int loggingSystemZmqPublishPort) {
		this.loggingSystemZmqPublishPort = loggingSystemZmqPublishPort;
	}

	public String getDataModelDateFormat() {
		return dataModelDateFormat;
	}

	public void setDataModelDateFormat(String dataModelDateFormat) {
		this.dataModelDateFormat = dataModelDateFormat;
	}

	@Override
	public String toString() {
		return "ConfigModel{" +
				"identifier='" + identifier + '\'' +
				", dataDefinitionURL='" + dataDefinitionURL + '\'' +
				", dataManagerURL='" + dataManagerURL + '\'' +
				", runtimeManagerURL='" + runtimeManagerURL + '\'' +
				", projectBuilderURL='" + projectBuilderURL + '\'' +
				", subscriberPort=" + subscriberPort +
				", requestPort=" + requestPort +
				", droolsDateFormat='" + droolsDateFormat + '\'' +
				'}';
	}
}
