package io.je.utilities.execution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.Utility;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.JavaCodeInjectionError;
import io.je.utilities.logger.*;
import io.je.utilities.monitoring.MessageModel;
import io.je.utilities.zmq.ZMQRequester;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

//import org.influxdb.InfluxDB;
//import org.influxdb.InfluxDBFactory;

public class Executioner {
    public static ObjectMapper objectMapper = new ObjectMapper();
    //private static final ExecutorService executor = Executors.newFixedThreadPool(10);


    private Executioner() {
    }


    /*
     * Execute inform block [ send log to logging system]
     */
    public static void informRuleBlock(String projectId, String ruleId, String message, String logDate, String BlockName) {
        try {
            new Thread(new Runnable() {


                @Override
                public void run() {

                    try {
                        LogMessage msg = new LogMessage(LogLevel.Inform, message, logDate, projectId,
                                 LogSubModule.RULE, BlockName);
                        ZMQLogPublisher.publish(msg);
                        //   JELogger.info(objectMapper.writeValueAsString(msg), LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);
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


    /*
     * trigger an event
     */
    public static void triggerEvent(String projectId, String eventId) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {

        try {
            new Thread(new Runnable() {


                @Override
                public void run() {

                    try {
                        JERunnerAPIHandler.triggerEvent(eventId, projectId);
                        JEBuilderApiHandler.triggerEvent(eventId, projectId);


                        // JELogger.info("Event was triggered", LogCategory.RUNTIME, projectId, LogSubModule.RULE, eventId);


                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }


    }

    /*
     * trigger an event + send event data to logging system
     */
    public static void triggerEvent(String projectId, String eventId, String eventName, String ruleId, String triggerSource) {

        try {
            new Thread(new Runnable() {


                @Override
                public void run() {

                    try {
                        JERunnerAPIHandler.triggerEvent(eventId, projectId);
                        // JEBuilderApiHandler.triggerEvent(eventId, projectId);

                        LogMessage msg = new LogMessage(LogLevel.Debug, eventName + " is triggered ", LocalDateTime.now().toString(),  projectId,
                                 LogSubModule.RULE, triggerSource);
                        ZMQLogPublisher.publish(msg);
                     
                    /* JEMessage message = new JEMessage();
                     message.setExecutionTime(LocalDateTime.now().toString());
                     message.setType("BlockMessage");
                     JEBlockMessage blockMessage = new JEBlockMessage(triggerSource,  eventName +" was triggered");
                     message.addBlockMessage(blockMessage);
                     JELogger.trace(objectMapper.writeValueAsString(message), LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);
*/

                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    public static void writeMonitoringMessageToInfluxDb(MessageModel messageModel) {
        //InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "io", "io.123");

    }


    public static void writeToInstance(String instanceId, String attributeName, Object value) {
        //Rework to use a callable for exception handling
        String request = generateDMWriteRequest(instanceId, attributeName, value);

        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    JELogger.debug(JEMessages.SENDING_REQUEST_TO_DATA_MODEL + " : " + request,  LogCategory.RUNTIME,
                            null, LogSubModule.RULE, null);
                    ZMQRequester requester = new ZMQRequester("tcp://" + Utility.getSiothConfig().getMachineCredentials().getIpAddress(), Utility.getSiothConfig().getDataModelPORTS().getDmService_ReqAddress());
                    String response = requester.sendRequest(request);
                    if (response == null) {
                        JELogger.error(getClass(), JEMessages.NO_RESPONSE_FROM_DATA_MODEL);
                    } else {
                        JELogger.info("Data Model Returned : " + response);
                    }


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }*/


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

    
    private static String generateDMWriteRequest(String instanceId, String attributeName, Object attributeNewValue) {
    	
    	HashMap<String, Object> payload = new HashMap<>();
    	payload.put("InstanceId", instanceId);   	
    	List<HashMap<String, Object>> attributesList = new ArrayList<>();
    	HashMap<String, Object> attributes = new HashMap<>();
    	attributes.put(attributeName, attributeNewValue);
    	attributesList.add(attributes);
    	payload.put("Attributes", attributesList);
    	String request= "";
    	try {
			 request = objectMapper.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			//JELogger.error : failed to generate request
		}
    	return request;
    }
    
   /* public static void main(String[] args) {
        executeScript("test", "", "");
    }*/
    public static void executeScript(String name, String processId, String projectId) throws JavaCodeInjectionError {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        //Task to be executed in a separate thread
        Future<Void> task = executor.submit(() -> {
            JEClassLoader.overrideInstance();
            Class<?> loadClass =JEClassLoader.getInstance().loadClass(ClassBuilderConfig.generationPackageName +"." + name);
            Method method = loadClass.getDeclaredMethods()[0];
            method.invoke(null);
            return null;
        });
        String msg = "Unknown error";
        try {
            task.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            msg = "Script task in workflow with id = " + processId + " in project with id = " + projectId + " failed during the execution";
            JELogger.error(msg, LogCategory.RUNTIME, projectId,
                    LogSubModule.JERUNNER, processId);
            throw new JavaCodeInjectionError(msg);
        } catch (InterruptedException e) {
            msg = "Script task in workflow with id = " + processId + " in project with id = " + projectId + " was interrupted during the execution";
            JELogger.error(msg, LogCategory.RUNTIME, projectId,
                    LogSubModule.JERUNNER, processId);
            if(Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
            }
            throw new JavaCodeInjectionError(msg);
        } catch (TimeoutException e) {
            msg = "Script task in workflow with id = " + processId + " in project with id = " + projectId + " Timed out the execution";
            JELogger.error(msg, LogCategory.RUNTIME, projectId,
                    LogSubModule.JERUNNER, processId);
            executor.shutdown();
            task.cancel(true);
            throw new JavaCodeInjectionError(msg);
        }


       /* ExecutorService executorService = Executors.newFixedThreadPool(1);
        Callable c = () -> {
            JEClassLoader.overrideInstance();
            Class<?> loadClass = JEClassLoader.getInstance().loadClass(ClassBuilderConfig.generationPackageName + "." + name);
            Method method = loadClass.getDeclaredMethods()[0];
            method.invoke(null);

            return null;
        };
        Future<Void> task = null;
        try {
            task = executorService.submit(c);

            task.get(3, TimeUnit.SECONDS);
            if(task.isCancelled()) {
                Thread.currentThread().stop();
            }
        }
        catch(TimeoutException e) {
            String msg = "Script task in workflow with id = " + processId + " in project with id = " + projectId + " Timed out the execution";
            //executorService.shutdown();
            //executorService.awaitTermination(2, TimeUnit.SECONDS);
            //executorService.shutdownNow();
            task.cancel(true);
            JELogger.error(msg);
            throw new JavaCodeInjectionError(msg);
        }
        catch(InterruptedException e) {
            String msg = "Script task in workflow with id = " + processId + " in project with id = " + projectId + " was interrupted during the execution";
            JELogger.error(msg);
            Thread.currentThread().stop();
            executorService.shutdownNow();
            throw e;
        }
        catch(ExecutionException e) {
            String msg = "Script task in workflow with id = " + processId + " in project with id = " + projectId + " failed during the execution";
            JELogger.error(msg);
            throw new JavaCodeInjectionError(msg);
        }
        catch(Exception e) {
            String msg = "Script task in workflow with id = " + processId + " in project with id = " + projectId + " was interrupted during the execution";
            JELogger.error(msg);
            Thread.currentThread().stop();
            executorService.shutdownNow();
            throw new JavaCodeInjectionError(msg);
        }
        finally {
            /*if(task != null) {
                task.cancel(true);
            }
        }*/

    }

    /* to be deleted
     *  public static void untriggerEvent(String projectId, String eventId)
    {
    	try {
            new Thread(new Runnable() {

            	
                @Override
                public void run() {
                	
                   try {
                	   JERunnerAPIHandler.untriggerEvent(eventId, projectId);
                       

				} catch (Exception e) {
					e.printStackTrace();

				}


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

     * 
     */

}
