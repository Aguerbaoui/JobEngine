package io.je.project.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataModelPORTS {
	
	@JsonProperty("DataModelConfigurationPublishingAddress")
	public int dmRestAPI_ConfigurationPubAddress;
	
	@JsonProperty("DataModelRequestAddress")
	public int dmRestAPI_ReqAddress;
	
	@JsonProperty("DataModelWriteAddress")
	public int dmService_ReqAddress;
	
	@JsonProperty("DataModelPublishAddress")
	public int dmService_PubAddress;
	private DataModelPORTS() {
		// TODO Auto-generated constructor stub
	}
	public int getDmRestAPI_ConfigurationPubAddress() {
		return dmRestAPI_ConfigurationPubAddress;
	}
	public void setDmRestAPI_ConfigurationPubAddress(int dmRestAPI_ConfigurationPubAddress) {
		this.dmRestAPI_ConfigurationPubAddress = dmRestAPI_ConfigurationPubAddress;
	}
	public int getDmRestAPI_ReqAddress() {
		return dmRestAPI_ReqAddress;
	}
	public void setDmRestAPI_ReqAddress(int dmRestAPI_ReqAddress) {
		this.dmRestAPI_ReqAddress = dmRestAPI_ReqAddress;
	}
	public int getDmService_ReqAddress() {
		return dmService_ReqAddress;
	}
	public void setDmService_ReqAddress(int dmService_ReqAddress) {
		this.dmService_ReqAddress = dmService_ReqAddress;
	}
	public int getDmService_PubAddress() {
		return dmService_PubAddress;
	}
	public void setDmService_PubAddress(int dmService_PubAddress) {
		this.dmService_PubAddress = dmService_PubAddress;
	}
	
	
	
       
       
}
