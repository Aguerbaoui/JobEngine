package io.je.utilities.network;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import io.je.utilities.logger.JELogger;

public class Network {

	private static OkHttpClient client = new OkHttpClient();

	public static void makeNetworkCallWithJsonBody(HashMap<String, String> json, String url) throws IOException {
		String jsonStr = "";
		try {
			jsonStr = new ObjectMapper().writeValueAsString(json);

		} catch (JsonProcessingException e) {
			JELogger.info(e.getMessage());
		}
		JELogger.info(jsonStr);
		RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);

		Request request = new Request.Builder().url(url).post(body).build();

		Call call = client.newCall(request);
		Response response = call.execute();
	}
	
	public static void makeNetworkCall(String url) throws IOException {

		Request request = new Request.Builder().url(url).get().build();
		Call call = client.newCall(request);
		JELogger.info(url);
		Response response = call.execute();
	}
}
