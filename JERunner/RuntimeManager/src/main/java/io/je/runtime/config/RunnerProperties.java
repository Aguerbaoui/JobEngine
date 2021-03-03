package io.je.runtime.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;



@ConfigurationProperties
@Configuration("RunnerProperties")
public class RunnerProperties {
	

	@Value("${jobenginerunner.log.path}")
	 String jeRunnerLogPath;

	public String getJeRunnerLogPath() {
		return jeRunnerLogPath;
	}
	public void setJeRunnerLogPath(String jeRunnerLogPath) {
		this.jeRunnerLogPath = jeRunnerLogPath;
	}
	
	
	


}
