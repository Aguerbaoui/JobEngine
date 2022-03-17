package io.je.utilities.apis;

import com.squareup.okhttp.Response;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.network.HttpMethod;
import utils.network.Network;

import java.io.IOException;
import java.util.HashMap;

public class DatabaseApiHandler {
    public static final String EXECUTE_DATABASE_COMMAND = "/nonsense/nonsenseagain/api/database/execute/siothdb";
    public static String url = "http://njendoubi-pc:14002"/*SIOTHConfigUtility.getSiothConfig().getApis().getDatabaseAPI().getAddress() */+ EXECUTE_DATABASE_COMMAND;

    public static String executeCommand(String dbId, String query) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("DBIdentifier", dbId);
        headers.put("Command", query);
        Network network = new Network.Builder(url).hasBody(false).hasHeaders(true).withHeaders(headers)
                .build();
        Response response = network.call();
        //System.out.println(response.body().string());
        /*JELogger.info(JEMessages.DB_API_RESPONSE + " = " + response.body().string(), LogCategory.RUNTIME,
                null, LogSubModule.WORKFLOW, null);*/
        String resp = response.body().string();
        return resp;
    }

}
