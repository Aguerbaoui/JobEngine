package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ProcessRunner {

    private static Runtime rt = Runtime.getRuntime();

    public static String executeCommandWithErrorOutput(String command) throws IOException, InterruptedException {
        String output = "";
        Process process = rt.exec(command);
        int exitCode = process.waitFor();
        StringBuilder errorTextBuilder = new StringBuilder();

        try (Reader reader = new BufferedReader(new InputStreamReader
                (process.getErrorStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                errorTextBuilder.append((char) c);
            }
        }

        if(errorTextBuilder.length() > 0) {
            output += errorTextBuilder.toString() + "\n";
            System.out.println(output);
        }

        return output;
    }

    public static long executeCommandWithPidOutput(String command) throws IOException, InterruptedException {
        String output = "";
        Process process = rt.exec(command);
        //process.waitFor(30, TimeUnit.SECONDS);
        long pid = process.pid();
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (process.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        if(textBuilder.length() > 0) {
            output += textBuilder.toString() + "\n";
        }

        StringBuilder errorTextBuilder = new StringBuilder();

        try (Reader reader = new BufferedReader(new InputStreamReader
                (process.getErrorStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                errorTextBuilder.append((char) c);
            }
        }

        if(errorTextBuilder.length() > 0) {
            output += errorTextBuilder.toString() + "\n";
            System.out.println(output);
        }
        return pid;
    }

    public static Process executeCommandWithProcessOutput(String command) throws IOException, InterruptedException {
        String output = "";
        Process process = rt.exec(command);
        //process.waitFor(30, TimeUnit.SECONDS);
        long pid = process.pid();
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (process.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        if(textBuilder.length() > 0) {
            output += textBuilder.toString() + "\n";
        }

        StringBuilder errorTextBuilder = new StringBuilder();

        try (Reader reader = new BufferedReader(new InputStreamReader
                (process.getErrorStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                errorTextBuilder.append((char) c);
            }
        }

        if(errorTextBuilder.length() > 0) {
            output += errorTextBuilder.toString() + "\n";
            System.out.println(output);
        }
        return process;
    }
}
