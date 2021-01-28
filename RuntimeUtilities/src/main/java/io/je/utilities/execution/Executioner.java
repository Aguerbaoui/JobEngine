package io.je.utilities.execution;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.exceptions.JERunnerUnreachableException;

import java.io.IOException;

public class Executioner {

    public static void triggerEvent(String projectId, String eventId, String message) throws JERunnerUnreachableException, IOException {
        JERunnerAPIHandler.triggerEvent(eventId, projectId);
    }
}
