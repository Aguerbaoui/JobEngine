package io.je.utilities.files;

import io.je.utilities.logger.JELogger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class JEFileUtils {

	public static void copyStringToFile(String bpmn20Xml, String fileName, String encoding) {

		File file = new File(fileName);
		try {
			FileUtils.writeStringToFile(file, bpmn20Xml, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getStringFromFile(String path) {
		String content = null;
		try {
			JELogger.trace(JEFileUtils.class, " Reading string from file = " + path);
			content = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			JELogger.error(JEFileUtils.class, Arrays.toString(e.getStackTrace()));
		}
		return content;
	}

	public static void deleteFilesForPathByPrefix(final String path, final String prefix) {
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
			JELogger.error(JEFileUtils.class, "Failed to delete file " );
		}

		
	}
}
