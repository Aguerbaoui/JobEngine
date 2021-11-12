package io.je.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import io.siothconfig.*;

import java.util.Locale;


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
}
