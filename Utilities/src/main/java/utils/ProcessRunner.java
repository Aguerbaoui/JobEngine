package utils;

import utils.files.FileUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ProcessRunner {

    private static Runtime rt = Runtime.getRuntime();

    private static String processDumpPath;

    private static boolean dumpOutput;

    public static String executeCommandWithErrorOutput(String command) throws IOException, InterruptedException {
        String output = "";
        Process process = rt.exec(command);
        int exitCode = process.waitFor();
        return dumpProcessOutput(process, command, false, true);

        //return output;
    }

    public static Thread executeCommandWithPidOutput(String command) throws IOException, InterruptedException {
        Process process = rt.exec(command);
        //process.waitFor(30, TimeUnit.SECONDS);
        long pid = process.pid();
        //dumpProcessOutput(process, command, true, true);
        Thread thread = new Thread(() -> {
            try {
                dumpProcessOutput(process, command, true, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setName(String.valueOf(pid));
        thread.start();

        return thread;
    }

    /*public static Process executeCommandWithProcessOutput(String command) throws IOException, InterruptedException {
        Process process = rt.exec(command);
        //process.waitFor(30, TimeUnit.SECONDS);
        long pid = process.pid();
        dumpProcessOutput(process, command, true, true);
        return process;
    }*/

    private static String dumpProcessOutput(Process process, String command, boolean executionOutput, boolean errorOutput) throws IOException {
        String output = "Executing command = " + command + "\n";

        if(executionOutput) {
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (process.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }

            if (textBuilder.length() > 0) {
                output += textBuilder.toString() + "\n";
            }
        }
        if(errorOutput) {
            StringBuilder errorTextBuilder = new StringBuilder();

            try (Reader reader = new BufferedReader(new InputStreamReader
                    (process.getErrorStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    errorTextBuilder.append((char) c);
                }
            }
            if (errorTextBuilder.length() > 0) {
                output += errorTextBuilder.toString() + "\n";
                //System.out.println(output);
            }
        }
        /**/
        if(dumpOutput) {
            try {
                FileUtilities.writeToFile(processDumpPath, output);
            }
            catch (Exception ignored) {}
        }
        return output;
    }
    public static String getProcessDumpPath() {
        return processDumpPath;
    }

    public static void setProcessDumpPath(String processDumpPath, boolean dumpOutput) {
        ProcessRunner.processDumpPath = processDumpPath;
        ProcessRunner.dumpOutput = dumpOutput;
    }
}
