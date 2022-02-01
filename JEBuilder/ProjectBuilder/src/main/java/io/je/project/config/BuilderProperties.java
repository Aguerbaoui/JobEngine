package io.je.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import io.siothconfig.*;

@ConfigurationProperties
@Configuration("BuilderProperties")
@PropertySource(SIOTHConfigurationConstants.APPLICATION_PROPERTIES_PATH)
public class BuilderProperties {

	@Value("${jobenginebuilder.log.path}")
	String jeBuilderLogPath;

	@Value("${jobenginebuilder.log.level}")
	String jeBuilderLogLevel;

	@Value("${use.ZMQ.Security}")
	Boolean useZmqSecurity;

	@Value("${ids4.issuer}")
	String issuer;

	@Value("${monitoring.port}")
	int monitoringPort;
	
	@Value("${sioth.id}")
	String siothId;

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


	public int getMonitoringPort() {
		return monitoringPort;
	}

	public void setMonitoringPort(int monitoringPort) {
		this.monitoringPort = monitoringPort;
	}

	public String getJeBuilderLogPath() {
		return jeBuilderLogPath;
	}

	public void setJeBuilderLogPath(String jeBuilderLogPath) {
		this.jeBuilderLogPath = jeBuilderLogPath;
	}

	public String getJeBuilderLogLevel() {
		return jeBuilderLogLevel;
	}

	public void setJeBuilderLogLevel(String jeBuilderLogLevel) {
		this.jeBuilderLogLevel = jeBuilderLogLevel;
	}

	public Boolean getUseZmqSecurity() {
		return useZmqSecurity;
	}

	public void setUseZmqSecurity(Boolean useZmqSecurity) {
		this.useZmqSecurity = useZmqSecurity;
	}

	public String getIssuer() {
		return issuer.toLowerCase();
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getSiothId() {
		return siothId;
	}

	public void setSiothId(String siothId) {
		this.siothId = siothId;
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
}
