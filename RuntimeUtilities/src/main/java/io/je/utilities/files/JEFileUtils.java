package io.je.utilities.files;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class JEFileUtils {

	public static void copyStringToFile(String bpmn20Xml, String fileName, String encoding) {
		
		File file = new File(fileName);
		try {
			FileUtils.writeStringToFile(file, bpmn20Xml, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	
}
