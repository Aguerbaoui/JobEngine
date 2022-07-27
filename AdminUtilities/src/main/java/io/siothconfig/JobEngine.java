package io.siothconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobEngine {
	
	@JsonProperty("JERunner")
	public String jeRunner;

	@JsonProperty("JEBuilder")
	public String jeBuilder;

	@JsonProperty("CheckHealth")
	public int checkHealthEveryMs;

	@JsonProperty("LibraryMaxFileSize")
	public String libraryMaxFileSize;

	// FIXME MaxMemory is it used?

	@JsonProperty("GeneratedClassesPath")
	public String generatedClassesPath;
	
	
	
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

	public int getLibraryMaxFileSize() {
		try {
			if (libraryMaxFileSize != null) {
				int size = Integer.valueOf(libraryMaxFileSize.substring(0, libraryMaxFileSize.indexOf("MB")));
				return size * 1048576;
			}  return 100 * 1048576;
		}
		catch (Exception e) {
			return 100 * 1048576;
		}
	}

	public void setLibraryMaxFileSize(String libraryMaxFileSize) {
		this.libraryMaxFileSize = libraryMaxFileSize;
	}

	public String getGeneratedClassesPath() {
		return generatedClassesPath;
	}

	public void setGeneratedClassesPath(String generatedClassesPath) {
		this.generatedClassesPath = generatedClassesPath;
	}
}
