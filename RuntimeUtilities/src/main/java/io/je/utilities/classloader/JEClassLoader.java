package io.je.utilities.classloader;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.logger.JELogger;

/*
 * class responsible for loading user defined classes
 */
public class JEClassLoader {

	
	/*
	 * generate .class file from an input file located at filePath in the loadPath
	 */
	public static void loadClass(String filePath, String loadPath) throws ClassLoadException {
		
		try {
			JELogger.info(JEClassLoader.class, " loadPath = " + loadPath);
			JELogger.info(JEClassLoader.class, " Filepath = "+ filePath);
			File sourceFile = new File(filePath);
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
			/*List<String> options = new ArrayList<String>();
			options.add("-classpath");
			StringBuilder sb = new StringBuilder();
			URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
			for (URL url : urlClassLoader.getURLs()){
				//JELogger.info(JEClassLoader.class, url.getFile().substring(1));
				sb.append(url.getFile().substring(1).replace("%20", " ")).append(File.pathSeparator);
			}*/
			//options.add("D:\\Job engine\\RuntimeUtilities\\target\\RuntimeUtilities-0.0.1.jar"); fixed the issue for runtime

			// slash issue Widnows Vs JAVA/Linux to be reviewed with the deployment environment
			//options.add(sb.toString().replace("/", "\\"));

			/*options.add("D:\\apache-tomcat-9.0.41\\webapps\\ProjectBuilder\\WEB-INF\\lib\\RuntimeUtilities-0.0.1.jar;D:\\apache-tomcat-9.0.41\\webapps\\ProjectBuilder\\WEB-INF\\lib\\jackson-databind-2.11.3.jar" +
					";D:\\apache-tomcat-9.0.41\\webapps\\ProjectBuilder\\WEB-INF\\lib\\jackson-core-2.11.3.jar;D:\\apache-tomcat-9.0.41\\webapps\\ProjectBuilder\\WEB-INF\\lib\\jackson-annotations-2.11.3.jar;");*/
			//JELogger.info(JEClassLoader.class, " options = " + sb.toString().replace("/", "\\"));
			// Specify where to put the generated .class files
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(loadPath)));
			// Compile the file
			compiler.getTask(null, fileManager, null, null, null,
					fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile))).call();
			fileManager.close();
		}catch (Exception e) {
			//TODO: move msg to error class
			e.printStackTrace();
			JELogger.info(JEClassLoader.class, e.getMessage());
			throw new ClassLoadException("failed to load class");
		}
		
	}
}