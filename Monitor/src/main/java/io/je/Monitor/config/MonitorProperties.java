package io.je.Monitor.config;

import io.siothconfig.SIOTHConfigurationConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties
@Configuration("MonitorProperties")
@PropertySource(SIOTHConfigurationConstants.APPLICATION_PROPERTIES_PATH)
public class MonitorProperties {

    @Value("${jobenginemonitor.log.path}")
    String jeMonitorLogPath;

    @Value("${jobenginemonitor.log.level}")
    String jeMonitorLogLevel;

    @Value("${use.ZMQ.Security}")
    Boolean useZmqSecurity;

    @Value("${monitoring.port}")
    int monitoringPort;

    public int getMonitoringPort() {
		return monitoringPort;
	}
	public void setMonitoringPort(int monitoringPort) {
		this.monitoringPort = monitoringPort;
	}
	public String getJeMonitorLogPath() {
        return jeMonitorLogPath;
    }
    public void setJeMonitorLogPath(String jeMonitorLogPath) {
        this.jeMonitorLogPath = jeMonitorLogPath;
    }
    public String getJeMonitorLogLevel() {
        return jeMonitorLogLevel;
    }
    public void setJeMonitorLogLevel(String jeMonitorLogLevel) {
        this.jeMonitorLogLevel = jeMonitorLogLevel;
    }
    public Boolean getUseZmqSecurity() {
        return useZmqSecurity;
    }
    public void setUseZmqSecurity(Boolean useZmqSecurity) {
        this.useZmqSecurity = useZmqSecurity;
    }

}
