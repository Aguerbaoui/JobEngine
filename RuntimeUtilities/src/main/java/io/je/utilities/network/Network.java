package io.je.utilities.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import io.je.utilities.logger.JELogger;

import java.io.IOException;

public class Network {

    private static final OkHttpClient client = new OkHttpClient();

    private Network() {
    }


    public static void makeNetworkCallWithJsonBody(Object json, String url) throws IOException {
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
        call.execute();
    }

    public static Response makeGetNetworkCallWithResponse(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);
        return call.execute();
    }


    public static Response makeNetworkCallWithJsonBodyWithResponse(Object json, String url) throws IOException {
        String jsonStr = "";
        try {
            jsonStr = new ObjectMapper().writeValueAsString(json);

        } catch (JsonProcessingException e) {
            JELogger.info(Network.class, e.getMessage());
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        return call.execute();
    }

    public static Response makeNetworkCallWithJsonObjectBodyWithResponse(Object json, String url) throws IOException {
        String jsonStr = "";
        try {
            jsonStr = new ObjectMapper().writeValueAsString(json);

        } catch (JsonProcessingException e) {
            JELogger.info(Network.class, e.getMessage());
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        return call.execute();
    }

    public static Response makeNetworkCallWithStringObjectBodyWithResponse(String json, String url) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        return call.execute();
    }


}
