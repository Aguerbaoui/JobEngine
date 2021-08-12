package io.je.utilities.files;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static io.je.utilities.constants.JEMessages.ADDING_JAR_FILE_TO_RUNNER;

public class JEFileUtils {

	public static void copyStringToFile(String bpmn20Xml, String fileName, String encoding) {

		File file = new File(fileName);
		try {
			FileUtils.writeStringToFile(file, bpmn20Xml, encoding);
		}  catch (Exception e) {
			JELogger.error(JEMessages.UNEXPECTED_ERROR + Arrays.toString(e.getStackTrace()) ,  LogCategory.DESIGN_MODE,
					null, LogSubModule.JEBUILDER, null);
		}

	}

	public static String getStringFromFile(String path) {
		String content = null;
		try {
			JELogger.debug(JEMessages.READING_FILE + path,
					LogCategory.RUNTIME, null,
					LogSubModule.JERUNNER, null);
			content = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			JELogger.error(JEMessages.UNEXPECTED_ERROR +  Arrays.toString(e.getStackTrace()), LogCategory.RUNTIME, null,
					LogSubModule.JEBUILDER, null);
		}
		return content;
	}

	public static void deleteFileFromPath(String path) {
		try {
			File file = new File(path);
		}
		catch (Exception e) {
			JELogger.error(JEMessages.DELETE_FILE_FAILED ,  LogCategory.DESIGN_MODE,
					null, LogSubModule.JEBUILDER, null);
		}
	}
	public static void deleteFilesInPathByPrefix(final String path, final String prefix) {
		try {
		File directory = new File(path);
		if(directory.listFiles()!=null)
		{for (File f : directory.listFiles()) {
			if (f.getName().startsWith(prefix)) {
				f.delete();

		} 
		}
		}
		}catch (Exception e) {
			JELogger.error(JEMessages.DELETE_FILE_FAILED ,  LogCategory.DESIGN_MODE,
					null, LogSubModule.JEBUILDER, null);
		}

		
	}
}
