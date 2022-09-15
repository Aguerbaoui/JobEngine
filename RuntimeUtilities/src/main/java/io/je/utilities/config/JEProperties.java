package io.je.utilities.config;

import org.springframework.beans.factory.annotation.Value;

public class JEProperties {

    @Value("${jobenginebuilder.log.path}")
    String jeBuilderLogPath;

    @Value("${jobenginebuilder.log.level}")
    String jeBuilderLogLevel;

    @Value("${jobenginerunner.log.path}")
    String jeRunnerLogPath;

    @Value("${jobenginerunner.log.level}")
    String jeRunnerLogLevel;

    @Value("${jobenginemonitor.log.path}")
    String jeMonitorLogPath;

    @Value("${jobenginemonitor.log.level}")
    String jeMonitorLogLevel;

    @Value("${ids4.issuer}")
    String issuer;

    @Value("${monitoring.port}")
    int monitoringPort;

    @Value("${sioth.id}")
    String siothId;

    @Value("${dev.environment}")
    boolean dev;

    @Value("${java.processdump}")
    boolean dumpJavaProcessExecution;


    @Value("${jobengine.processesdump.path}")
    String processesDumpPath;

    //ZMQ Config
    @Value("${use.ZMQ.Security}")
    Boolean useZmqSecurity;

    @Value("${zmq.heartbeat.timeout}")
    int zmqHeartbeatTimeout;

    @Value("${zmq.handshake.interval}")
    int zmqHandshakeInterval;

    @Value("${zmq.receive.timeout}")
    int zmqReceiveTimeout;

    @Value("${zmq.send.timeout}")
    int zmqSendTimeout;

    @Value("${zmq.receive.high.watermark}")
    int zmqReceiveHighWatermark;

    @Value("${zmq.send.high.watermark}")
    int zmqSendHighWatermark;

    @Value("${jobenginerunner.zmq.responsePort}")
    int jeRunnerZMQResponsePort;


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
        return issuer.toLowerCase(); //?HA: fixing auth problem with machine name?
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


    public String getProcessesDumpPath() {
        return processesDumpPath;
    }

    public void setProcessesDumpPath(String processesDumpPath) {
        this.processesDumpPath = processesDumpPath;
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

    public int getZmqHeartbeatTimeout() {
        return zmqHeartbeatTimeout;
    }

    public void setZmqHeartbeatTimeout(int zmqHeartbeatTimeout) {
        this.zmqHeartbeatTimeout = zmqHeartbeatTimeout;
    }

    public int getZmqHandshakeInterval() {
        return zmqHandshakeInterval;
    }

    public void setZmqHandshakeInterval(int zmqHeartbeatInterval) {
        this.zmqHandshakeInterval = zmqHeartbeatInterval;
    }

    public int getZmqReceiveTimeout() {
        return zmqReceiveTimeout;
    }

    public void setZmqReceiveTimeout(int zmqReceiveTimeout) {
        this.zmqReceiveTimeout = zmqReceiveTimeout;
    }

    public int getZmqSendTimeout() {
        return zmqSendTimeout;
    }

    public void setZmqSendTimeout(int zmqSendTimeout) {
        this.zmqSendTimeout = zmqSendTimeout;
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

    public int getJeRunnerZMQResponsePort() {
        return jeRunnerZMQResponsePort;
    }

    public void setJeRunnerZMQResponsePort(int jeRunnerZMQResponsePort) {
        this.jeRunnerZMQResponsePort = jeRunnerZMQResponsePort;
    }


}
