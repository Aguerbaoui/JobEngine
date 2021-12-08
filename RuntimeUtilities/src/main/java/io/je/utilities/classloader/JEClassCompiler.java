package io.je.utilities.classloader;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

/*
 * class responsible for loading user defined classes
 */
public class JEClassCompiler {

	static String loadPath =  ConfigurationConstants.RUNNER_CLASS_LOAD_PATH;
	static String generationPath = ConfigurationConstants.JAVA_GENERATION_PATH;

	
	
	/*
	 * generate .class file from an input file located at filePath in the loadPath
	 */
	public static void compileClass(String filePath, String loadPath) throws ClassLoadException {
		//ClassLoadException exception = null;
		String message = "";
		try {
			JELogger.debug("loadPath = " + loadPath, LogCategory.RUNTIME,
					null, LogSubModule.JERUNNER, null);
			JELogger.debug("Filepath = "+ filePath, LogCategory.RUNTIME,
					null, LogSubModule.JERUNNER, null);
			File sourceFile = new File(filePath);
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
			List<String> options = new ArrayList<String>();

			options.add("-Xlint:-unchecked");
			options.add("-Xlint:-rawtypes");
			options.add("-Xlint:deprecation");

			/*StringBuilder sb = new StringBuilder();
			options.add("-classpath");
			URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
			for (URL url : urlClassLoader.getURLs()){
				//JELogger.info(JEClassLoader.class, url.getFile().substring(1));
				sb.append(url.getFile().substring(1).replace("%20", " ")).append(File.pathSeparator);
			}
			//options.add("D:\\Job engine\\RuntimeUtilities\\target\\RuntimeUtilities-0.0.1.jar"); fixed the issue for runtime

			// slash issue Widnows Vs JAVA/Linux to be reviewed with the deployment environment
			options.add(sb.toString().replace("/", "\\"));

			/*options.add("D:\\apache-tomcat-9.0.41\\webapps\\ProjectBuilder\\WEB-INF\\lib\\RuntimeUtilities-0.0.1.jar;D:\\apache-tomcat-9.0.41\\webapps\\ProjectBuilder\\WEB-INF\\lib\\jackson-databind-2.11.3.jar" +
					";D:\\apache-tomcat-9.0.41\\webapps\\ProjectBuilder\\WEB-INF\\lib\\jackson-core-2.11.3.jar;D:\\apache-tomcat-9.0.41\\webapps\\ProjectBuilder\\WEB-INF\\lib\\jackson-annotations-2.11.3.jar;");*/
			//JELogger.info(JEClassLoader.class, " options = " + sb.toString().replace("/", "\\"));
			// Specify where to put the generated .class files
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(loadPath)));
			// Compile the file
			Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
			DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<>();
			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollector, null, null,
					compilationUnit);
			if(task.call()) {
				JELogger.debug(JEMessages.CUSTOM_COMPILATION_SUCCESS, LogCategory.RUNTIME,
						null, LogSubModule.JERUNNER, null);
			}
			else {

				List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticsCollector.getDiagnostics();
				for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
					// read error details from the diagnostic object
					message = diagnostic.getMessage(null) ;

				}
			}
			fileManager.close();
		}catch (Exception e) {
			JELogger.error(JEMessages.UNEXPECTED_ERROR + Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME,
					null, LogSubModule.JERUNNER, null);
		}
		if(!message.isEmpty()) {
			ClassLoadException exception = new ClassLoadException(JEMessages.CLASS_LOAD_FAILED);
			JELogger.debug(message);
			exception.setCompilationErrorMessage(message);
			throw exception;
		}

	}
}
