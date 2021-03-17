package io.je.runtime.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;



@ConfigurationProperties
@Configuration("RunnerProperties")
@PropertySource("file:${SIOTHJobEngine}/jobengine.properties")
public class RunnerProperties {
	

	@Value("${jobenginerunner.log.path}")
	 String jeRunnerLogPath;
	
	@Value("${jobenginerunner.log.level}")
	 String jeRunnerLogLevel;

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
	
	
	


}
