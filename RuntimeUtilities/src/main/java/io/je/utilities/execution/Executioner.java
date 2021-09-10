package io.je.utilities.execution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.project.variables.VariableManager;
import io.je.utilities.apis.JEBuilderApiHandler;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.Utility;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.JavaCodeInjectionError;
import io.je.utilities.instances.DataModelRequester;
import io.je.utilities.instances.InstanceManager;
import io.je.utilities.logger.*;
import io.je.utilities.monitoring.MessageModel;
import io.je.utilities.runtimeobject.JEObject;
import io.je.utilities.zmq.ZMQRequester;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;



public class Executioner {
    public static ObjectMapper objectMapper = new ObjectMapper();
    //private static final ExecutorService executor = Executors.newFixedThreadPool(10);


    private Executioner() {
    }


    /*************************************** INFORM *********************************************************/
    
    /*
     * Execute inform block 
     */
    public static void informRuleBlock(String projectId, String ruleId, String message, String logDate, String BlockName) {
        try {
            new Thread(new Runnable() {


                @Override
                public void run() {

                    try {
                    	
                        JELogger.info(message, LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);
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
    /*************************************************** SETTERS *******************************************************************/
    /*************************************** SET DATA MODEL INSTANCE VALUE *********************************************************/
    /*****SET FROM STATIC VALUE*****/
    public static void updateInstanceAttributeValueFromStaticValue(String instanceId, String attributeName, Object value) {
        //Rework to use a callable for exception handling

        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                   InstanceManager.writeToDataModelInstance(instanceId,attributeName,value);


                }
            }).start();
        } catch (Exception e) {
        	JELogger.error(JEMessages.WRITE_INSTANCE_FAILED ,  LogCategory.RUNTIME,
                    null, LogSubModule.RULE, null);
        }

    }

    /*****SET FROM VARIABLE*****/
    /*
     * update instance attribute from  variable
     */
    public static void updateInstanceAttributeValueFromVariable(String projectId,String instanceId,String attributeName ,String variableId) {

        try {
            new Thread(new Runnable() {


                @Override
                public void run() {

                    try {
                         
                         Object attribueValue = VariableManager.getVariableValue(projectId, variableId);
                         InstanceManager.writeToDataModelInstance(instanceId,attributeName,attribueValue);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /*****SET FROM ANOTHER DATAMODEL INSTANCE*****/
    public static void updateInstanceAttributeValueFromAnotherInstance(String projectId,String ruleId, String sourceInstanceId,String sourceAttributeName,String destinationInstanceId, String destinationAttributeName)
    {
    	try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    JEObject sourceInstance = InstanceManager.getInstance(sourceInstanceId);
                    if(sourceInstance==null)
                    {
                        JELogger.error("Failed to read instance value", null, projectId, LogSubModule.RULE, sourceInstanceId);
                    	return;
                    }
                    
                    Object attribueValue = InstanceManager.getAttributeValue(sourceInstanceId, sourceAttributeName);
                    InstanceManager.writeToDataModelInstance(destinationInstanceId,destinationAttributeName,attribueValue);




                }
            }).start();
        } catch (Exception e) {
           JELogger.error("Failed to set instance value", null, projectId, LogSubModule.RULE, ruleId);
        }
    }

    /*************************************** SET VARIABLE VALUE *********************************************************/
    /*****SET FROM STATIC VALUE*****/
    /*
     * update Variable from a static value
     */
    public static void updateVariableValue(String projectId,String variableId, Object value) {

        try {
            new Thread(new Runnable() {


                @Override
                public void run() {

                    try {
                        VariableManager.updateVariableValue(projectId, variableId, value);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /*****SET FROM VARIABLE*****/
    
    /*
     * update Variable from another variable
     */
    public static void updateVariableValueFromAnotherVariable(String projectId,String sourceVariableId, String destinationVariableId) {

        try {
            new Thread(new Runnable() {


                @Override
                public void run() {

                    try {
                        VariableManager.updateVariableValue(projectId, destinationVariableId, VariableManager.getVariableValue(projectId, sourceVariableId));
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    
    /*****SET FROM ANOTHER DATAMODEL INSTANCE*****/
  
    /*
     * update Variable from a data model instance
     */
    public static void updateVariableValueFromDataModel(String projectId,String variableId, String instanceId, String attributeName) {

        try {
            new Thread(new Runnable() {


                @Override
                public void run() {

                    try {
                        Object attribueValue = InstanceManager.getAttributeValue(instanceId, attributeName);
                        VariableManager.updateVariableValue(projectId, variableId, attribueValue);
                  
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    
    
   

    

    /*************************************** EVENT *********************************************************/

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
                  
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                }
            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }



   

    


    
   
    
   /* public static void main(String[] args) {
        executeScript("test", "", "");
    }*/
    public static void executeScript(String name, String processId, String projectId, int timeout) throws JavaCodeInjectionError {
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
            if(timeout > 0) {
                task.get(timeout, TimeUnit.SECONDS);
            }
            else {
                task.get(600, TimeUnit.SECONDS);
            }
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
