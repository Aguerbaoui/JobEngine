package io.je.utilities.execution;

import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEMessage;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.monitoring.MessageModel;
import io.je.utilities.zmq.ZMQRequester;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;

//import org.influxdb.InfluxDB;
//import org.influxdb.InfluxDBFactory;

public class Executioner {
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private static 	ObjectMapper objectMapper = new ObjectMapper();


    private Executioner() {
    }
    
    public static void informRuleBlock(String projectId, String ruleId, JEMessage msg)
    {
    	try {
            new Thread(new Runnable() {

            	
                @Override
                public void run() {
                	
                   try {
                       JELogger.info(objectMapper.writeValueAsString(msg), LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);
				} catch (Exception e) {
					e.printStackTrace();
                    JELogger.error("Failed to execute Inform Block", LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);

				}


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static void triggerEvent(String projectId, String eventId) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {

        JERunnerAPIHandler.triggerEvent(eventId, projectId);
    	
    /*	Runnable runnable =  () -> {
			try {
				JERunnerAPIHandler.triggerEvent(eventId, projectId);
				
			} catch (JERunnerErrorException | IOException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
				JELogger.error(Executioner.class, "failed to trigger event");
			}
		};
	*/


    }

    public static void writeMonitoringMessageToInfluxDb(MessageModel messageModel) {
        //InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "io", "io.123");

    }


    public static void writeToInstance(String instanceId, String attributeName, String value) {
        //Rework to use a callable for exception handling
        String request = generateRequest(instanceId, attributeName, value);

        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    JELogger.debug(JEMessages.SENDING_REQUEST_TO_DATA_MODEL + " : " + request);
                    ZMQRequester requester = new ZMQRequester(JEConfiguration.getDataManagerURL(), JEConfiguration.getRequestPort());
                    String response = requester.sendRequest(request);
                    if (response == null) {
                        JELogger.error(getClass(), JEMessages.NO_RESPONSE_FROM_DATA_MODEL);
                    } else {
                        JELogger.info("Data Model Returned" + response);
                    }


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }


    /*
     * generate data model write request
     */
    private static String generateRequest(String instanceId, String attributeName, String attributeNewValue) {
        String req = "{\r\n"
                + "   \"InstanceId\":\"" + instanceId + "\",\r\n"
                + "   \"Attributes\":[\r\n"
                + "      {\r\n"
                + "         \"Name\":\"" + attributeName + "\",\r\n"
                + "         \"Value\":\"" + attributeNewValue + "\"\r\n"
                + "      }\r\n"
                + "   ]\r\n"
                + "}";
        return req;
    }

    public static void executeScript(String name) throws Exception {
      /*  JEClassLoader loader = new JEClassLoader(
                Executioner.class.getClassLoader());
        Class<?> loadClass =
                loader.loadClass("classes." + name);
        Method method
                = loadClass.getDeclaredMethods()[0];
        method.invoke(null);
*/
        executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               /* Class<?> clazz = null;
                clazz = Class.forName("classes." + name);
                ClassLoader classLoader = this.getClass().getClassLoader();
                URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new File("D:\\Job engine\\JERunner\\RuntimeManager\\target\\classes").toURI().toURL()});
                Class loadClass = urlClassLoader.loadClass("classes." + name);*/
                JEClassLoader loader = new JEClassLoader(
                        Executioner.class.getClassLoader());
                Class<?> loadClass =
                        loader.loadClass("classes." + name);
                Method method
                        = loadClass.getDeclaredMethods()[0];
                method.invoke(null);
                return null;
            }
        }).get();


    }


}
