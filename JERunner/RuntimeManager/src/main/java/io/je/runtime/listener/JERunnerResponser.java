package io.je.runtime.listener;

import java.util.HashMap;

import io.je.utilities.models.VariableModel;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.project.exception.JEExceptionHandler;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEZMQResponse;
import io.je.utilities.beans.RunnerRequestObject;
import io.je.utilities.beans.ZMQResponseType;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import io.je.utilities.mapping.VariableModelMapping;
import utils.log.LogSubModule;
import utils.zmq.ZMQBind;
import utils.zmq.ZMQResponser;

public class JERunnerResponser extends ZMQResponser {

	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();

	RuntimeDispatcher runtimeDispatcher = new RuntimeDispatcher();

	public JERunnerResponser(String url, int repPort, ZMQBind bind) {
		super(url, repPort, bind);
	}

	public JERunnerResponser() {
		super();
	}

	public void init(String url, int repPort, ZMQBind bind) {
		this.url = url;
		this.repPort = repPort;
		this.bindType = bind;
	}

	@Override
	public void run() {
		while (isListening()) {
			JEZMQResponse response = new JEZMQResponse(ZMQResponseType.FAIL);
			RunnerRequestObject request;
			try {
				String data = this.getRepSocket(ZMQBind.BIND).recvStr(0);
				if (data != null && !data.isEmpty() && !data.equals("null")) {

					JELogger.info(JEMessages.ZMQ_REQUEST_RECEIVED + data, null, null, LogSubModule.JERUNNER, null);

					request = objectMapper.readValue(data, RunnerRequestObject.class);

					switch (request.getRequest()) {
					case UPDATE_VARIABLE:
						response = updateVariable(request.getRequestBody());
					case GET_VARIABLE:
						response = readVariable(request.getRequestBody());

						break;
					default:
						response.setErrorMessage(JEMessages.UNKNOWN_REQUEST);
						break;

					}
					sendResponse(response);

				}
			} catch (Exception e) {
				String errorMsg = JEExceptionHandler.getExceptionMessage(e);
				JELogger.error(JEMessages.ZMQ_FAILED_TO_RESPOND + errorMsg, null, null, LogSubModule.JERUNNER, null);

			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private JEZMQResponse updateVariable(Object requestBody) {

		try {
			HashMap<String, Object> body = (HashMap<String, Object>) requestBody;

			runtimeDispatcher.writeVariableValue((String) body.get(VariableModelMapping.PROJECT_ID),
					(String) body.get(VariableModelMapping.VARIABLE_ID),
					String.valueOf(body.get(VariableModelMapping.VALUE)),
					(boolean) body.get(VariableModelMapping.IGNORE_IF_SAME_VALUE));
		} catch (Exception e) {
			e.printStackTrace();
			return new JEZMQResponse(ZMQResponseType.FAIL, e.getMessage());
		}

		return new JEZMQResponse(ZMQResponseType.SUCCESS);

	}

	private JEZMQResponse readVariable(Object requestBody) {
		try {
			HashMap<String, Object> body = (HashMap<String, Object>) requestBody;
			JEZMQResponse rep = new JEZMQResponse(ZMQResponseType.SUCCESS);
			var variable = runtimeDispatcher.getVariable((String) body.get(VariableModelMapping.PROJECT_ID),
					(String) body.get(VariableModelMapping.VARIABLE_ID));
			rep.setResponseObject(objectMapper.writeValueAsString(variable));

			return rep;
		} catch (Exception e) {
			e.printStackTrace();
			return new JEZMQResponse(ZMQResponseType.FAIL, e.getMessage());
		}

	}

	private void sendResponse(JEZMQResponse response) {

		try {
			JELogger.debug(JEMessages.ZMQ_SENDING_RESPONSE + objectMapper.writeValueAsString(response), null, null, LogSubModule.JERUNNER, null);

			this.getRepSocket(ZMQBind.BIND).send(objectMapper.writeValueAsString(response));
		} catch (Exception e) {
			JELogger.error(JEMessages.ZMQ_FAILED_TO_RESPOND + e.getMessage(), null, null, LogSubModule.JERUNNER, null);
		}

	}

}
