package utils.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import utils.log.LoggerUtils;
import io.netty.resolver.DefaultAddressResolverGroup;
import okhttp3.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class Network {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String TOKEN = "token";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private static final OkHttpClient client = new OkHttpClient();
    private static ThreadPoolTaskExecutor executor = null;
    private BodyType bodyType;
    private String body;
    private boolean hasBody;
    private String url;
    private boolean hasParameters;
    //private boolean isAuthenticated;
    private AuthScheme authScheme;
    private HashMap<String, String> authentication;
    private HashMap<String, String> parameters;
    private boolean hasHeaders;
    private HashMap<String, String> headers;

    private Network() {
    }

    /*
     * Async executor for network calls
     * */
    public static Executor getAsyncExecutor() {

        if (executor == null) {
            executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(3);
            executor.setMaxPoolSize(100);
            executor.setQueueCapacity(150);
            executor.setThreadNamePrefix("NetworkAsyncThread-");
            executor.initialize();
            client.newBuilder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .build();
        }
        return executor;
    }

    /*
     * Get with response
     * */
    public static Response makeGetNetworkCallWithResponse(String url) throws InterruptedException, ExecutionException {
        Request request = new Request.Builder().url(url)
                .get()
                .build();
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request)
                        .execute();
            } catch (IOException exp) {
                LoggerUtils.logException(exp);
                return null;
            }
        }, getAsyncExecutor());
        return f.get();
    }

    /*
     * Delete with response
     * */
    public static Response makeMultipartFormDataPost(String url, String fileName, String filePath) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(null, new File(filePath)))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();

    }

    public static Response makeDeleteNetworkCallWithResponse(String url) throws InterruptedException, ExecutionException {
        Request request = new Request.Builder().url(url)
                .delete()
                .build();
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request)
                        .execute();
            } catch (IOException exp) {
                LoggerUtils.logException(exp);
                return null;
            }
        }, getAsyncExecutor());
        return f.get();
    }

    /*
     * Post with json body
     * */
    public static Response makeNetworkCallWithJsonBodyWithResponse(Object json, String url) throws IOException, InterruptedException, ExecutionException {
        String jsonStr = "";
        if (json instanceof String) {
            jsonStr = (String) json;
        } else {
            jsonStr = new ObjectMapper().writeValueAsString(json);
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        Request request = new Request.Builder().url(url)
                .post(body)
                .build();
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request)
                        .execute();
            } catch (IOException exp) {
                LoggerUtils.logException(exp);
                return null;
            }
        }, getAsyncExecutor());
        return f.get();
    }

    /*
     * Patch with json body
     * */
    public static Response makePatchNetworkCallWithJsonBodyWithResponse(Object json, String url) throws IOException, InterruptedException, ExecutionException {
        String jsonStr = "";

        jsonStr = new ObjectMapper().writeValueAsString(json);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        Request request = new Request.Builder().url(url)
                .patch(body)
                .build();
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request)
                        .execute();
            } catch (IOException exp) {
                LoggerUtils.logException(exp);
                return null;
            }
        }, getAsyncExecutor());
        return f.get();
    }

    /*
     * Post with string body
     * */
    public static Response makeNetworkCallWithStringObjectBodyWithResponse(String json, String url) throws ExecutionException, InterruptedException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder().url(url)
                .post(body)
                .build();
        CompletableFuture<Response> f = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request)
                        .execute();
            } catch (IOException exp) {
                LoggerUtils.logException(exp);
                return null;
            }
        }, getAsyncExecutor());
        return f.get();
    }

    public Response call() throws IOException {

        RequestBody requestBody = null;
        Request request = null;
        Request.Builder builder = null;

        if (hasParameters) {
            HttpUrl.Builder httpBuilder = HttpUrl.parse(url)
                    .newBuilder();
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
            builder = new Request.Builder().url(httpBuilder.build());
        } else {
            builder = new Request.Builder().url(url);
        }
        if (hasBody) {
            if (bodyType == BodyType.JSON) {
                body = body.replace("=", ":");
                requestBody = RequestBody.create(MediaType.parse("application/json"), body);
            }
            builder.post(requestBody);
        } else {
            builder.get();
        }

        if (authScheme == AuthScheme.BASIC) {
            String credential = Credentials.basic(authentication.get(USERNAME), authentication.get(PASSWORD));
            builder.header(AUTHORIZATION, credential);
        } else if (authScheme == AuthScheme.BEARER) {
            String token = BEARER + authentication.get(TOKEN);
            builder.addHeader(AUTHORIZATION, token);
        } else if (authScheme == AuthScheme.API_KEY) {
            String key = authentication.get(KEY);
            String value = authentication.get(VALUE);
            builder.addHeader(key, value);
        }

        if (hasHeaders) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        request = builder.build();

        return client.newCall(request).execute();
    }

    /*
     * Execute network call
     * */
    public static HashMap<String, Object> makePostWebClientRequest(String url, String json) throws IOException {

        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json; utf-8")
                .build();

        HashMap<String, Object> responseMap = new HashMap<>();
        ResponseEntity<Void> emailResponse;
        try {
            emailResponse = webClient.post()

                    .header(HttpHeaders.CONTENT_TYPE, "application/json; utf-8")
                    .bodyValue(json)
                    .retrieve()

                    // error body as String or other class
                    .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                            .flatMap(res -> {
                                responseMap.put("code", response.statusCode());
                                return Mono.error(new RuntimeException(res));
                            }))

                    .toBodilessEntity()
                    .block();
        } catch (Exception exp) {
            LoggerUtils.logException(exp);
            responseMap.put("message", exp.getMessage());
            return responseMap;
        }

        if (emailResponse != null) {
            responseMap.put("message", emailResponse);
            responseMap.put("code", emailResponse.getStatusCode());
        }
/*        responseMap.put("code", con.getResponseCode());
        responseMap.put("message", response.toString());*/
        return responseMap;
    }

    /*
     * Network object builder
     * */
    public static class Builder {
        private final String url;
        private HttpMethod method;
        private BodyType bodyType;
        private String body;
        private String classType;
        private boolean hasParameters;
        private boolean hasBody;
        private HashMap<String, String> authentication;
        private AuthScheme authScheme;
        private HashMap<String, String> parameters;
        private boolean hasHeaders;
        private HashMap<String, String> headers;

        public Builder(String url) {
            this.url = url;
        }

        public Builder withHeaders(HashMap<String, String> headers) {
            if (headers != null) this.headers = headers;
            return this;
        }

        public Builder hasHeaders(boolean hasHeaders) {
            this.hasHeaders = hasHeaders;
            return this;
        }

        public Builder withAuthentication(HashMap<String, String> authentication) {
            if (authentication != null) this.authentication = authentication;
            return this;
        }

        public Builder withAuthScheme(AuthScheme scheme) {
            this.authScheme = scheme;
            return this;
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
            network.hasBody = this.hasBody;
            network.url = this.url;
            network.hasParameters = this.hasParameters;
            network.parameters = this.parameters;
            network.authScheme = this.authScheme;
            network.authentication = this.authentication;
            network.headers = this.headers;
            network.hasHeaders = this.hasHeaders;
            //network.isAuthenticated = this.isAuthenticated;
            return network;
        }

    }

}
