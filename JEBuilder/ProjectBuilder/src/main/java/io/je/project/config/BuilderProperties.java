package io.je.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;



@ConfigurationProperties
@Configuration("BuilderProperties")
public class BuilderProperties {
	
	@Value("${jobenginebuilder.log.path}")
	 String jeBuilderLogPath;

	public String getJeBuilderLogPath() {
		return jeBuilderLogPath;
	}
	public void setJeBuilderLogPath(String jeBuilderLogPath) {
		this.jeBuilderLogPath = jeBuilderLogPath;
	}

	
	
	


}
