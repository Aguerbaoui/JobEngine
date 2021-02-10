package io.je.utilities.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import io.je.utilities.logger.JELogger;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class Network {

    private static final OkHttpClient client = new OkHttpClient();

    private Network() {
    }

    public static Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("NetworkAsynchThread-");
		executor.initialize();
		return executor;
	}

    public static Response makeGetNetworkCallWithResponse(String url) throws IOException, InterruptedException, ExecutionException {
        Request request = new Request.Builder().url(url).get().build();
        CompletableFuture<Call> f = CompletableFuture.supplyAsync(() -> {
            return client.newCall(request);
        },getAsyncExecutor());
        return f.get().execute();
    }


    public static Response makeNetworkCallWithJsonBodyWithResponse(Object json, String url) throws IOException, InterruptedException, ExecutionException {
        String jsonStr = "";
        try {
            jsonStr = new ObjectMapper().writeValueAsString(json);

        } catch (JsonProcessingException e) {
            JELogger.info(Network.class, e.getMessage());
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        Request request = new Request.Builder().url(url).post(body).build();
        CompletableFuture<Call> f = CompletableFuture.supplyAsync(() -> {
            return client.newCall(request);
        },getAsyncExecutor());
        return f.get().execute();
    }



    public static Response makeNetworkCallWithStringObjectBodyWithResponse(String json, String url) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        return call.execute();
    }


}
