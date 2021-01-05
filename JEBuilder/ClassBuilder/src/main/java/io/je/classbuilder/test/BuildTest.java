package io.je.classbuilder.test;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.classbuilder.builder.ClassBuilder;
import io.je.classbuilder.models.ClassModel;
import io.je.utilities.exceptions.ClassFormatInvalidException;
import io.je.utilities.network.Network;

public class BuildTest {

	public static void main(String[] args) throws IOException, ClassFormatInvalidException {
		ObjectMapper objectMapper = new ObjectMapper();
		Response resp = Network.makeNetworkCallWithResponse("http://localhost:5555/api/Class/14/workspace/123");
		ClassModel jeClass = objectMapper.readValue(resp.body().string(), ClassModel.class);
		jeClass.setWorkspaceId("14");
		System.out.println(jeClass);
		ClassBuilder.buildClass(jeClass, "C:\\JobEngine");

	}

}
