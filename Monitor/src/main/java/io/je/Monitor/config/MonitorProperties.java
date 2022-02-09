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
    
	@Value("${sioth.id}")
	String siothId;

    @Value("${java.prcessdump}")
    boolean dumpJavaProcessExecution;

    @Value("${dev.environment}")
    boolean dev;

    @Value("${jobenginemonitor.url}")
    String monitorUrl;

    @Value("${jobengine.processesdump.path}")
    String processesDumpPath;

    @Value("${jobenginebuilder.url}")
    String builderUrl;

    @Value("${jobenginerunner.url}")
    String runnerUrl;

    public String getSiothId() {
		return siothId;
	}
	public void setSiothId(String siothId) {
		this.siothId = siothId;
	}
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

    public boolean isDev() {
        return dev;
    }

    public void setDev(boolean dev) {
        this.dev = dev;
    }

    public String getMonitorUrl() {
        return monitorUrl;
    }

    public void setMonitorUrl(String monitorUrl) {
        this.monitorUrl = monitorUrl;
    }

    public String getProcessesDumpPath() {
        return processesDumpPath;
    }

    public void setProcessesDumpPath(String processesDumpPath) {
        this.processesDumpPath = processesDumpPath;
    }

    public String getBuilderUrl() {
        return builderUrl;
    }

    public void setBuilderUrl(String builderUrl) {
        this.builderUrl = builderUrl;
    }

    public String getRunnerUrl() {
        return runnerUrl;
    }

    public void setRunnerUrl(String runnerUrl) {
        this.runnerUrl = runnerUrl;
    }

    public boolean isDumpJavaProcessExecution() {
        return dumpJavaProcessExecution;
    }

    public void setDumpJavaProcessExecution(boolean dumpJavaProcessExecution) {
        this.dumpJavaProcessExecution = dumpJavaProcessExecution;
    }
}
