package io.je.utilities.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import io.je.utilities.apis.BodyType;
import io.je.utilities.apis.HttpMethod;
import io.je.utilities.logger.JELogger;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class Network {

    private static final OkHttpClient client = new OkHttpClient();

    private HttpMethod method;

    private BodyType bodyType;

    private String body;

    private boolean hasBody;

    private String classType;

    private String url;

    private Network() {
    }
    private static ThreadPoolTaskExecutor executor = null;
    public static Executor getAsyncExecutor() {

        if(executor == null) {
            executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(3);
            executor.setMaxPoolSize(10);
            executor.setQueueCapacity(10);
            executor.setThreadNamePrefix("NetworkAsynchThread-");
            executor.initialize();
        }
		return executor;
	}

    public static Response makeGetNetworkCallWithResponse(String url) throws IOException, InterruptedException, ExecutionException {
        JELogger.info(" Making Get network call to url = " + url);
        Request request = new Request.Builder().url(url).get().build();
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request).execute();
            } catch (IOException e) {
                return null;
            }
        },getAsyncExecutor());
        return f.get();
    }

    public static Response makeDeleteNetworkCallWithResponse(String url) throws IOException, InterruptedException, ExecutionException {
        Request request = new Request.Builder().url(url).delete().build();
        JELogger.trace(" Making Delete network call to url = " + url);
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request).execute();
            } catch (IOException e) {
                return null;
            }
        },getAsyncExecutor());
        return f.get();
    }

    public static Response makeNetworkCallWithJsonBodyWithResponse(Object json, String url) throws IOException, InterruptedException, ExecutionException {
        String jsonStr = "";
        try {
            jsonStr = new ObjectMapper().writeValueAsString(json);

        } catch (JsonProcessingException e) {
            JELogger.info(Network.class, e.getMessage());
        }
        JELogger.trace(" Making POST network call to url = " + url);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        Request request = new Request.Builder().url(url).post(body).build();
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request).execute();
            } catch (IOException e) {
                return null;
            }
        },getAsyncExecutor());
        return f.get();
    }



    public static Response makeNetworkCallWithStringObjectBodyWithResponse(String json, String url) throws IOException, ExecutionException, InterruptedException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(url).post(body).build();
        JELogger.trace(" Making network String POST call to url = " + url);
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request).execute();
            } catch (IOException e) {
                return null;
            }
        },getAsyncExecutor());
        return f.get();
    }



    /*
    * Network object builder
    * */
    public static class Builder {
        private HttpMethod method;
        private BodyType bodyType;
        private String body;
        private String classType;
        private String url;
        private boolean hasBody;

        public Builder withMethod(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder (String url) {
            this.url = url;
        }

        public Builder hasBody(boolean hasBody) {
            this.hasBody = hasBody;
            return this;
        }
        public Builder withBodyType(BodyType bodyType) {
            this.bodyType = bodyType;
            return this;
        }

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public Builder toClass(String classType) {
            this.classType = classType;
            return this;
        }

        public Network build() {
            Network network = new Network();
            network.body = this.body;
            network.bodyType = this.bodyType;
            network.classType = this.classType;
            network.hasBody = this.hasBody;
            network.url = this.url;
            network.method = this.method;
            return network;
        }
    }

    public Response call() throws IOException {

        RequestBody requestBody = null;
        Request request = null;
        if (hasBody) {
            if (bodyType == BodyType.JSON) {
                body = body.replace("=", ":");
                requestBody = RequestBody.create(MediaType.parse("application/json"), body);
            }

            request = new Request.Builder().url(url).post(requestBody).build();
        }
        else {
            request = new Request.Builder().url(url).get().build();
        }

        OkHttpClient client = new OkHttpClient();
        return client.newCall(request).execute();
    }
}
