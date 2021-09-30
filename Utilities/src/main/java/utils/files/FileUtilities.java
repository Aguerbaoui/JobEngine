package utils.files;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileUtilities {

	public static void copyStringToFile(String bpmn20Xml, String fileName, String encoding) throws IOException {

		File file = new File(fileName);

			FileUtils.writeStringToFile(file, bpmn20Xml, encoding);


	}

	public static String getStringFromFile(String path) throws IOException {
		String content = null;


			content = new String(Files.readAllBytes(Paths.get(path)));

		return content;
	}

	public static void deleteFileFromPath(String path) {

			File file = new File(path);


	}
	public static void deleteFilesInPathByPrefix(final String path, final String prefix) {

		File directory = new File(path);
		if(directory.listFiles()!=null)
		{for (File f : directory.listFiles()) {
			if (f.getName().startsWith(prefix)) {
				f.delete();

		} 
		}
		}


		
	}
}
