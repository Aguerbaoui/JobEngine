package io.je.utilities.classLoader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/*
 * class responsible for loading user defined classes
 */
public class ClassLoader {

	
	/*
	 * generate .class file from an input file located at filePath in the loadPath
	 */
	public static void loadClass(String filePath, String loadPath) throws IOException {
		
		File sourceFile = new File(filePath);
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		// Specify where to put the generated .class files
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(loadPath)));
		// Compile the file
		compiler.getTask(null, fileManager, null, null, null,
				fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile))).call();
		fileManager.close();
	}
}
