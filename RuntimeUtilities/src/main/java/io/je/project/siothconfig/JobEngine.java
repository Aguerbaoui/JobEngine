package io.je.project.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobEngine {
	
	@JsonProperty("JERunner")
	public String jeRunner;

	@JsonProperty("JEBuilder")
	public String jeBuilder;

	@JsonProperty("CheckHealth")
	public int checkHealthEveryMs;
	
	
	
	private JobEngine() {
		// TODO Auto-generated constructor stub
	}



	public String getJeRunner() {
		return jeRunner;
	}



	public void setJeRunner(String jeRunner) {
		this.jeRunner = jeRunner;
	}



	public String getJeBuilder() {
		return jeBuilder;
	}



	public void setJeBuilder(String jeBuilder) {
		this.jeBuilder = jeBuilder;
	}

	public int getCheckHealthEveryMs() {
		return checkHealthEveryMs;
	}

	public void setCheckHealthEveryMs(int checkHealthEveryMs) {
		this.checkHealthEveryMs = checkHealthEveryMs;
	}
}
