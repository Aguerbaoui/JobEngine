package utils.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class Network {

    private static final OkHttpClient client = new OkHttpClient();
    private static ThreadPoolTaskExecutor executor = null;
    private HttpMethod method;
    private BodyType bodyType;
    private String body;
    private boolean hasBody;
    private String classType;
    private String url;
    private boolean hasParameters;
    private HashMap<String, String> parameters;

    private Network() {
    }

    public static Executor getAsyncExecutor() {

        if (executor == null) {
            executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(3);
            executor.setMaxPoolSize(10);
            executor.setQueueCapacity(10);
            executor.setThreadNamePrefix("NetworkAsyncThread-");
            executor.initialize();
        }
        return executor;
    }

    public static Response makeGetNetworkCallWithResponse(String url) throws IOException, InterruptedException, ExecutionException {
        Request request = new Request.Builder().url(url).get().build();
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request).execute();
            } catch (IOException e) {
                return null;
            }
        }, getAsyncExecutor());
        return f.get();
    }

    public static Response makeDeleteNetworkCallWithResponse(String url) throws IOException, InterruptedException, ExecutionException {
        Request request = new Request.Builder().url(url).delete().build();
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request).execute();
            } catch (IOException e) {
                return null;
            }
        }, getAsyncExecutor());
        return f.get();
    }

    public static Response makeNetworkCallWithJsonBodyWithResponse(Object json, String url) throws IOException, InterruptedException, ExecutionException {
        String jsonStr = "";

            jsonStr = new ObjectMapper().writeValueAsString(json);

        /*} catch (JsonProcessingException e) {
            /*JELogger.error("Json parsing error" + e.getMessage(),
                    LogCategory.RUNTIME, null,
                    LogSubModule.JERUNNER,null);

        }
        JELogger.debug(JEMessages.NETWORK_POST + url,
                LogCategory.RUNTIME, null,
                LogSubModule.JERUNNER,null);*/
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        Request request = new Request.Builder().url(url).post(body).build();
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request).execute();
            } catch (IOException e) {
                return null;
            }
        }, getAsyncExecutor());
        return f.get();
    }


    public static Response makeNetworkCallWithStringObjectBodyWithResponse(String json, String url) throws IOException, ExecutionException, InterruptedException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(url).post(body).build();
        /*JELogger.debug(JEMessages.NETWORK_POST + url,
                LogCategory.RUNTIME, null,
                LogSubModule.JERUNNER,null);*/
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request).execute();
            } catch (IOException e) {
                return null;
            }
        }, getAsyncExecutor());
        return f.get();
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
        } else {
            if (hasParameters) {
                HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
                for (Map.Entry<String, String> param : parameters.entrySet()) {
                    httpBuilder.addQueryParameter(param.getKey(), param.getValue());
                }

                request = new Request.Builder().url(httpBuilder.build()).build();
            } else {
                request = new Request.Builder().url(url).get().build();
            }
        }

        OkHttpClient client = new OkHttpClient();
        return client.newCall(request).execute();
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
        private boolean hasParameters;
        private boolean hasBody;
        private HashMap<String, String> parameters;

        public Builder(String url) {
            this.url = url;
        }

        public Builder hasParameters(boolean hasParameters) {
            this.hasParameters = hasParameters;
            return this;
        }

        public Builder withParam(String key, String param) {
            if (this.parameters == null) this.parameters = new HashMap<>();
            this.parameters.put(key, param);
            return this;
        }

        public Builder withMethod(HttpMethod method) {
            this.method = method;
            return this;
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
            network.hasParameters = this.hasParameters;
            network.parameters = this.parameters;
            return network;
        }
    }
}
