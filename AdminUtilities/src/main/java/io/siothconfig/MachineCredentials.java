package io.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineCredentials {

    @JsonProperty("IPAddress")
    private String ipAddress;

    private MachineCredentials() {
        // TODO Auto-generated constructor stub
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String iPAddress) {
        this.ipAddress = iPAddress;
    }


}
