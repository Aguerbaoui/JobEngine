package io.je.utilities.execution;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.exceptions.JERunnerErrorException;

import java.io.IOException;

public class Executioner {

    public static void triggerEvent(String projectId, String eventId, String message) throws JERunnerErrorException, IOException {
        JERunnerAPIHandler.triggerEvent(eventId, projectId);
    }
}
