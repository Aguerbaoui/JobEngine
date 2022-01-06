package utils.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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

	public static void deleteFileFromPath(String path) throws IOException {
		Files.deleteIfExists(Paths.get(path));
	}

	public static void deleteDirectory(String path) {

		File directory = new File(path);
		// if the file is directory or not
		if(directory.isDirectory()) {
			File[] files = directory.listFiles();

			// if the directory contains any file
			if(files != null) {
				for(File file : files) {

					// recursive call if the subdirectory is non-empty
					deleteDirectory(file.getAbsolutePath());
				}
			}
		}

		/*if(directory.delete()) {
			System.out.println(directory + " is deleted");
		}
		else {
			System.out.println("Directory not deleted");
		}*/
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

	public static String getFileExtension(String fileName) {
		return FilenameUtils.getExtension(fileName);
	}

	public static boolean fileIsJar(String fileName) {
		return getFileExtension(fileName).equals("jar");
	}
}
