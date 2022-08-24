package io.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SIOTHPorts {

    @JsonProperty("NodeManagerPort")
    public int nodeManagerPort;

    @JsonProperty("LogServicePort")
    public int logServicePort;

    @JsonProperty("MonitoringPort")
    public int monitoringPort;

    @JsonProperty("MonitoringPubPort")
    public int monitoringPubPort;

    @JsonProperty("SignalRHubPort")
    public int signalRHubPort;

    @JsonProperty("SIOTHLicensePort")
    public int siothLicensePort;

    @JsonProperty("TrackingPort")
    public int trackingPort;

    @JsonProperty("JE_ResponsePort")
    public int jeResponsePort;


    private SIOTHPorts() {
        // TODO Auto-generated constructor stub
    }

    public int getNodeManagerPort() {
        return nodeManagerPort;
    }

    public void setNodeManagerPort(int nodeManagerPort) {
        this.nodeManagerPort = nodeManagerPort;
    }

    public int getLogServicePort() {
        return logServicePort;
    }

    public void setLogServicePort(int logServicePort) {
        this.logServicePort = logServicePort;
    }

    public int getMonitoringPort() {
        return monitoringPort;
    }

    public void setMonitoringPort(int monitoringPort) {
        this.monitoringPort = monitoringPort;
    }

    public int getMonitoringPubPort() {
        return monitoringPubPort;
    }

    public void setMonitoringPubPort(int monitoringPubPort) {
        this.monitoringPubPort = monitoringPubPort;
    }

    public int getSignalRHubPort() {
        return signalRHubPort;
    }

    public void setSignalRHubPort(int signalRHubPort) {
        this.signalRHubPort = signalRHubPort;
    }

    public int getSiothLicensePort() {
        return siothLicensePort;
    }

    public void setSiothLicensePort(int siothLicensePort) {
        this.siothLicensePort = siothLicensePort;
    }

    public int getTrackingPort() {
        return trackingPort;
    }

    public void setTrackingPort(int trackingPort) {
        this.trackingPort = trackingPort;
    }

    public int getJeResponsePort() {
        return jeResponsePort;
    }

    public void setJeResponsePort(int jeResponsePort) {
        this.jeResponsePort = jeResponsePort;
    }


}
