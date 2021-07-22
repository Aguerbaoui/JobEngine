package io.je.project.siothconfig;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Connectors {
	

	@JsonProperty("Protocols")
    public List<String> protocols ;
	
	@JsonProperty("Databases")
	public List<String> databases ;
    
	@JsonProperty("NetworkWatchers")
	public List<String> networkWatchers ;
    
	@JsonProperty("Brokers")
	public List<String> brokers ;

	
	private Connectors() {
		// TODO Auto-generated constructor stub
	}


	public List<String> getProtocols() {
		return protocols;
	}


	public void setProtocols(List<String> protocols) {
		this.protocols = protocols;
	}


	public List<String> getDatabases() {
		return databases;
	}


	public void setDatabases(List<String> databases) {
		this.databases = databases;
	}


	public List<String> getNetworkWatchers() {
		return networkWatchers;
	}


	public void setNetworkWatchers(List<String> networkWatchers) {
		this.networkWatchers = networkWatchers;
	}


	public List<String> getBrokers() {
		return brokers;
	}


	public void setBrokers(List<String> brokers) {
		this.brokers = brokers;
	}
	
	

}
