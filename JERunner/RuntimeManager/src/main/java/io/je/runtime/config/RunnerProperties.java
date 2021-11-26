package io.je.runtime.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import io.siothconfig.*;

@ConfigurationProperties
@Configuration("RunnerProperties")
@PropertySource(SIOTHConfigurationConstants.APPLICATION_PROPERTIES_PATH)
public class RunnerProperties {

	@Value("${jobenginerunner.log.path}")
	String jeRunnerLogPath;

	@Value("${jobenginerunner.log.level}")
	String jeRunnerLogLevel;

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

	public String getJeRunnerLogPath() {
		return jeRunnerLogPath;
	}

	public void setJeRunnerLogPath(String jeRunnerLogPath) {
		this.jeRunnerLogPath = jeRunnerLogPath;
	}

	public String getJeRunnerLogLevel() {
		return jeRunnerLogLevel;
	}

	public void setJeRunnerLogLevel(String jeRunnerLogLevel) {
		this.jeRunnerLogLevel = jeRunnerLogLevel;
	}

	public Boolean getUseZmqSecurity() {
		return useZmqSecurity;
	}

	public void setUseZmqSecurity(Boolean useZmqSecurity) {
		this.useZmqSecurity = useZmqSecurity;
	}

}
