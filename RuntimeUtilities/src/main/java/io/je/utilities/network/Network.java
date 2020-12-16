package io.je.utilities.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import io.je.utilities.logger.JELogger;

import java.io.IOException;
import java.util.HashMap;

public class Network {

    private static OkHttpClient client = new OkHttpClient();

    private Network() {
    }

    public static void makeNetworkCallWithJsonBody(HashMap<String, String> json, String url) throws IOException {
        String jsonStr = "";
        try {
            jsonStr = new ObjectMapper().writeValueAsString(json);

        } catch (JsonProcessingException e) {
            JELogger.info(Network.class, e.getMessage());
        }
        JELogger.info(Network.class, jsonStr);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);

        Request request = new Request.Builder().url(url).post(body).build();

        Call call = client.newCall(request);
        call.execute();
    }

    public static void makeNetworkCall(String url) throws IOException {

        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);
        JELogger.info(Network.class, url);
        call.execute();
    }
}
