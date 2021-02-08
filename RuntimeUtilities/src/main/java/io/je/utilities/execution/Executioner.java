package io.je.utilities.execution;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.exceptions.JERunnerErrorException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Executioner {

    public static void triggerEvent(String projectId, String eventId) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        JERunnerAPIHandler.triggerEvent(eventId, projectId);
    }
}
