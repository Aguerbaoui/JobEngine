package io.je.utilities.classloader;


import java.io.File;
import java.lang.reflect.Modifier;
import java.util.*;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.logger.JELogger;
import org.burningwave.core.Virtual;
import org.burningwave.core.classes.ClassSourceGenerator;
import org.burningwave.core.classes.FunctionSourceGenerator;
import org.burningwave.core.classes.TypeDeclarationSourceGenerator;
import org.burningwave.core.classes.UnitSourceGenerator;

/*
 * class responsible for loading user defined classes
 */
public class JEClassLoader {

	static String loadPath =  ConfigurationConstants.runnerClassLoadPath;
	static String generationPath = ConfigurationConstants.classGenerationPath;

	
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
			List<String> options = new ArrayList<String>();
			options.add("-classpath");
			StringBuilder sb = new StringBuilder();
			/*URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
			for (URL url : urlClassLoader.getURLs()){
				//JELogger.info(JEClassLoader.class, url.getFile().substring(1));
				sb.append(url.getFile().substring(1).replace("%20", " ")).append(File.pathSeparator);
			}
			//options.add("D:\\Job engine\\RuntimeUtilities\\target\\RuntimeUtilities-0.0.1.jar"); fixed the issue for runtime

			// slash issue Widnows Vs JAVA/Linux to be reviewed with the deployment environment
			options.add(sb.toString().replace("/", "\\"));*/

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
			//TODO: move msg to error clas
			e.printStackTrace();
			JELogger.info(JEClassLoader.class, e.getMessage());
			throw new ClassLoadException(JEMessages.CLASS_LOAD_FAILED);
		}

	}
	public static void generateScriptTaskClass(String name, String javaCode) {
		UnitSourceGenerator unitSG = UnitSourceGenerator.create(ClassBuilderConfig.genrationPackageName).addClass(
				ClassSourceGenerator.create(
						TypeDeclarationSourceGenerator.create(name)
				).addModifier(
						Modifier.PUBLIC
				).addMethod(
						FunctionSourceGenerator.create("executeScript")
								.setReturnType(
										TypeDeclarationSourceGenerator.create(void.class)
								)
								.addModifier(Modifier.PUBLIC)
								.addModifier(Modifier.STATIC)
								.addBodyCodeLine(javaCode)
				).addConcretizedType(Virtual.class));
		unitSG.addImport("io.je.utilities.logger.JELogger");
		unitSG.addImport("java.lang.*");
		unitSG.addImport("java.util.*");
		unitSG.addImport("java.sql.*");
		unitSG.addImport("javax.sql.*");

		System.out.println(unitSG.make());
		String filePath= generationPath + "\\" + ClassBuilderConfig.genrationPackageName  + "\\" + name +".java" ;
		File file = new File(generationPath);
		file.delete();
		unitSG.storeToClassPath(generationPath);
		try {
			JEClassLoader.loadClass(filePath, loadPath);
		} catch (ClassLoadException e) {
			e.printStackTrace();
		}
		/*try {
			Class<?> clazz = Class.forName("classes." + name);
			Method method
					= clazz.getDeclaredMethods()[0];
			method.invoke(null);
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}*/
	}
	public static void main(String args[]) {
		String a = "C:\\Program Files\\Integration Objects\\Integration Objects' SmartIoT Highway\\Components\\Tomcat";
		a = a.substring(0, a.indexOf("Components") -1);
		a = a + "\\JobEngine\\Builder\\properties";
		System.out.println(a);
	}
}
