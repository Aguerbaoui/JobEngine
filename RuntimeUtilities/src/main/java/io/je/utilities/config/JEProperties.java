package io.je.utilities.config;

import org.springframework.beans.factory.annotation.Value;

public class JEProperties {

	@Value("${jobenginebuilder.log.path}")
	String jeBuilderLogPath;

	@Value("${jobenginebuilder.log.level}")
	String jeBuilderLogLevel;

	@Value("${ids4.issuer}")
	String issuer;

	@Value("${monitoring.port}")
	int monitoringPort;
	
	@Value("${sioth.id}")
	String siothId;

	@Value("${dev.environment}")
	boolean dev;

	@Value("${java.prcessdump}")
	boolean dumpJavaProcessExecution;

	@Value("${jobenginemonitor.url}")
	String monitorUrl;

	@Value("${jobengine.processesdump.path}")
	String processesDumpPath;

	@Value("${jobenginebuilder.url}")
	String builderUrl;

	@Value("${jobenginerunner.url}")
	String runnerUrl;

	@Value("${jobenginerunner.log.path}")
	String jeRunnerLogPath;

	@Value("${jobenginerunner.log.level}")
	String jeRunnerLogLevel;
	
    @Value("${jobenginemonitor.log.path}")
    String jeMonitorLogPath;

    @Value("${jobenginemonitor.log.level}")
    String jeMonitorLogLevel;
	
	//ZMQ Config
	@Value("${use.ZMQ.Security}")
	Boolean useZmqSecurity;

	@Value("${zmq.heartbeat.value}")
	int zmqHeartbeatValue;

	@Value("${zmq.heartbeat.interval}")
	int zmqHeartbeatInterval;

	@Value("${zmq.receive.interval}")
	int zmqReceiveInterval;

	@Value("${zmq.receive.high.watermark}")
	int zmqReceiveHighWatermark;

	@Value("${zmq.send.high.watermark}")
	int zmqSendHighWatermark;


	

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
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public int getMonitoringPort() {
		return monitoringPort;
	}

	public void setMonitoringPort(int monitoringPort) {
		this.monitoringPort = monitoringPort;
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

	public boolean isDumpJavaProcessExecution() {
		return dumpJavaProcessExecution;
	}

	public void setDumpJavaProcessExecution(boolean dumpJavaProcessExecution) {
		this.dumpJavaProcessExecution = dumpJavaProcessExecution;
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

	public int getZmqHeartbeatValue() {
		return zmqHeartbeatValue;
	}

	public void setZmqHeartbeatValue(int zmqHeartbeatValue) {
		this.zmqHeartbeatValue = zmqHeartbeatValue;
	}

	public int getZmqHeartbeatInterval() {
		return zmqHeartbeatInterval;
	}

	public void setZmqHeartbeatInterval(int zmqHeartbeatInterval) {
		this.zmqHeartbeatInterval = zmqHeartbeatInterval;
	}

	public int getZmqReceiveInterval() {
		return zmqReceiveInterval;
	}

	public void setZmqReceiveInterval(int zmqReceiveInterval) {
		this.zmqReceiveInterval = zmqReceiveInterval;
	}

	public int getZmqReceiveHighWatermark() {
		return zmqReceiveHighWatermark;
	}

	public void setZmqReceiveHighWatermark(int zmqReceiveHighWatermark) {
		this.zmqReceiveHighWatermark = zmqReceiveHighWatermark;
	}

	public int getZmqSendHighWatermark() {
		return zmqSendHighWatermark;
	}

	public void setZmqSendHighWatermark(int zmqSendHighWatermark) {
		this.zmqSendHighWatermark = zmqSendHighWatermark;
	}
}
