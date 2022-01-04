package commands;

import io.je.utilities.beans.ClassAuthor;
import io.je.utilities.beans.JEClass;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.log.JELogger;
import utils.ProcessRunner;
import utils.files.FileUtilities;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static io.je.utilities.config.ConfigurationConstants.BUILDER_CLASS_LOAD_PATH;
import static io.je.utilities.config.ConfigurationConstants.JAVA_GENERATION_PATH;

public class CommandExecutioner {

    public static final String JAVA = "java ";

    public static final String JAR = "jar ";

    public static final String JAVAC = "javac ";

    public static final String CP = "-cp";

    public static final String CVF = "cvf";

    public static final String TASKKILL_PID = "taskkill /F /T /PID ";

    private static Runtime rt = Runtime.getRuntime();

    public static final String classpathFolder = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE) + "\\..\\Job Engine\\libs\\*";

    public static void compileCode(String filePath, boolean currentClassPath) throws InterruptedException, IOException, ClassLoadException {
        currentClassPath = false;
        String command = currentClassPath ? JAVAC + " " + CP + " \"" + classpathFolder + getCurrentClassPath() + "\" " + "\"" + filePath + "\" "
                :
                JAVAC + " " + CP + " \"" + classpathFolder + "\" " + "\"" + filePath + "\" ";
        String errorTextBuilder =  ProcessRunner.executeCommandWithErrorOutput(command);
        if(errorTextBuilder.length() > 0) {
            String error = errorTextBuilder.substring(errorTextBuilder.indexOf("error:"));
            ClassLoadException exception = new ClassLoadException(JEMessages.CLASS_LOAD_FAILED);
            JELogger.debug(error);
            exception.setCompilationErrorMessage(error);
            throw exception;
        }
    }

    public static long runCode(String filePath) throws IOException, InterruptedException {
        String command = JAVA + " " + CP + " " + classpathFolder  + " " + filePath;
        return ProcessRunner.executeCommandWithPidOutput(command);
        //return p.pid();
    }

    public static void KillProcessByPid(long pid) throws IOException, InterruptedException {
        String command = TASKKILL_PID + pid;
        ProcessRunner.executeCommandWithProcessOutput(command);
    }

    public static void buildJar() throws IOException, InterruptedException {
        StringBuilder command = new StringBuilder(JAR + " " + CVF + " \"" + ConfigurationConstants.EXTERNAL_LIB_PATH + "JEUtils.jar\"");
        command.append(" \"").append(JAVA_GENERATION_PATH).append("\\jeclasses\"");
        FileUtilities.deleteFileFromPath(ConfigurationConstants.EXTERNAL_LIB_PATH + "JEUtils.jar");
        ProcessRunner.executeCommandWithPidOutput(command.toString());
    }

    public static String getCurrentClassPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(File.pathSeparator);
        URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        for (URL url : urlClassLoader.getURLs()){
            //JELogger.info(JEClassLoader.class, url.getFile().substring(1));
            sb.append(url.getFile().substring(1).replace("%20", " ")).append(File.pathSeparator);
        }
        return sb.toString().replace("/", "\\");
    }
}
