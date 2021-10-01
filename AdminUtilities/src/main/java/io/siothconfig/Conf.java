package io.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Conf {
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("Credentials")
	private Cred credentials;
	private Conf() {
		super();
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Cred getCredentials() {
		return credentials;
	}
	public void setCredentials(Cred credentials) {
		this.credentials = credentials;
	}
	
	
	

}
