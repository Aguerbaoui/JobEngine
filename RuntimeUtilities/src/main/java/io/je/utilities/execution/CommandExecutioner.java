package io.je.utilities.execution;

import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.log.JELogger;
import utils.ProcessRunner;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import static io.je.utilities.config.ConfigurationConstants.JAVA_GENERATION_PATH;
import static io.je.utilities.constants.JEMessages.ERROR_BUILDING_JAR_FILE_AFTER_COMPILING_CLASSES_CHECK_ONGOING_PROCESSES;

public class CommandExecutioner {

    public static final String JAVA = "java ";

    public static final String JAR = "jar ";

    public static final String JAVAC = "javac -Xlint:unchecked -Xlint:-rawtypes -Xlint:deprecation -Xdiags:verbose";

    public static final String CP = "-cp";

    public static final String CVF = "cvf";

    public static final String TASKKILL_PID = "taskkill /F /T /PID ";

    private static Runtime rt = Runtime.getRuntime();

    public static final String classpathFolder = System.getenv(ConfigurationConstants.SIOTH_ENVIRONMENT_VARIABLE) + "\\..\\Job Engine\\libs\\*";

    public static void compileCode(String filePath, boolean currentClassPath) throws InterruptedException, IOException, ClassLoadException {
        //currentClassPath = false;
        String command = !currentClassPath ? JAVAC + " " + CP + " \"" + classpathFolder + "\" " + "\"" + filePath + "\" "
                :
                JAVAC + " " + CP + " \"" + classpathFolder + "\" " + "\"" + filePath + "\" ";
        String errorTextBuilder =  ProcessRunner.executeCommandWithErrorOutput(command);
        if(errorTextBuilder.length() > 0 && errorTextBuilder.indexOf("error:") != -1) {
            String error = errorTextBuilder.substring(errorTextBuilder.indexOf("error:"));
            String errorMsg = JEMessages.CLASS_LOAD_FAILED;
            if(error.indexOf("error: unreachable statement") != -1) {
                errorMsg += " " + "because of unreachable statement (infinite loop)";
            }
            ClassLoadException exception = new ClassLoadException(errorMsg);
            JELogger.debug(error);
            exception.setCompilationErrorMessage(error);
            throw exception;
        }
    }

    public static Thread runCode(String filePath) throws IOException, InterruptedException {
        String command = JAVA + " " + CP + " \"" + classpathFolder  + getCurrentClassPath() +  "\" \"" + filePath + "\"";
        return ProcessRunner.executeCommandWithPidOutput(command);
        //return p.pid();
    }

    public static void KillProcessByPid(long pid) throws IOException, InterruptedException {
        String command = TASKKILL_PID + pid;
        ProcessRunner.executeCommandWithPidOutput(command);
    }

    public static void buildJar() throws IOException, InterruptedException {
        StringBuilder command = new StringBuilder(JAR + " " + CVF + " \"" + ConfigurationConstants.EXTERNAL_LIB_PATH + "JEUtils.jar\"");
        command.append(" \"").append(JAVA_GENERATION_PATH).append("\\jeclasses\"");
        try {
            FileUtilities.deleteFileFromPath(ConfigurationConstants.EXTERNAL_LIB_PATH + "JEUtils.jar");
        }
        catch (Exception e) {
            JELogger.error(ERROR_BUILDING_JAR_FILE_AFTER_COMPILING_CLASSES_CHECK_ONGOING_PROCESSES, LogCategory.DESIGN_MODE, "", LogSubModule.JEBUILDER, "");
        }
        ProcessRunner.executeCommandWithPidOutput(command.toString());
    }

    public static String getCurrentClassPath() {
        return "";

        //If we want to load libraries loaded in tomcat memory

        /*StringBuilder sb = new StringBuilder();
        sb.append(File.pathSeparator);
        URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        for (URL url : urlClassLoader.getURLs()){
            //JELogger.info(JEClassLoader.class, url.getFile().substring(1));
            sb.append(url.getFile().substring(1).replace("%20", " ")).append(File.pathSeparator);
        }
        return sb.toString().replace("/", "\\");*/
    }
}
