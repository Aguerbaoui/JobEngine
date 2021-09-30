package io.je.utilities.apis;

import com.squareup.okhttp.Response;
import io.je.utilities.config.Utility;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import utils.network.HttpMethod;
import utils.network.Network;

import java.io.IOException;

public class DatabaseApiHandler {
    public static final String EXECUTE_DATABASE_COMMAND = "/api/DBBridge/Execute";
    public static String url = Utility.getSiothConfig().getApis().getDatabaseAPI().getAddress() + EXECUTE_DATABASE_COMMAND;

    public static int executeCommand(String dbId, String query) throws IOException {
        Network network = new Network.Builder(url).hasBody(false).hasParameters(true).withParam("DBIdentifier", dbId)
                .withMethod(HttpMethod.GET).withParam("Command", query)
                .build();
        Response response = network.call();
        JELogger.info(JEMessages.DB_API_RESPONSE + " = " + response.body().string(), LogCategory.RUNTIME,
                null, LogSubModule.WORKFLOW, null);
        return response.code();
    }

}
