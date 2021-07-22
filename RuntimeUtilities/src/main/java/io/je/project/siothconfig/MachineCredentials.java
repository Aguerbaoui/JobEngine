package io.je.project.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineCredentials {

	private MachineCredentials() {
		// TODO Auto-generated constructor stub
	}

    @JsonProperty("IPAddress")
	private String ipAddress;

    

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String iPAddress) {
		this.ipAddress = iPAddress;
	}


	
}
