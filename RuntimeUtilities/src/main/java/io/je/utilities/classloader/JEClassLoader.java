package io.je.utilities.classloader;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
			JELogger.info(JEClassLoader.class, "loading class from " + loadPath);

			File sourceFile = new File(filePath);
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

			// Specify where to put the generated .class files
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(loadPath)));
			// Compile the file
			compiler.getTask(null, fileManager, null, null, null,
					fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile))).call();
			fileManager.close();
		}catch (Exception e) {
			//TODO: move msg to error class
			JELogger.info(JEClassLoader.class, e.getMessage());
			throw new ClassLoadException("failed to load class");
		}
		
	}
}