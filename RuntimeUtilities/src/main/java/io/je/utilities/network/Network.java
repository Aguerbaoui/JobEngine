package io.je.utilities.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import io.je.utilities.logger.JELogger;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

public class Network {

    private static final OkHttpClient client = new OkHttpClient();

    private Network() {
    }


    @Async
    public static CompletableFuture<Response> makeGetNetworkCallWithResponse(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);
        return CompletableFuture.completedFuture(call.execute());
    }

    @Async
    public static CompletableFuture<Response> makeNetworkCallWithJsonBodyWithResponse(Object json, String url) throws IOException {
        String jsonStr = "";
        try {
            jsonStr = new ObjectMapper().writeValueAsString(json);

        } catch (JsonProcessingException e) {
            JELogger.info(Network.class, e.getMessage());
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        return CompletableFuture.completedFuture(call.execute());
    }

  

}
