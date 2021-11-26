package io.je.utilities.execution;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.project.variables.VariableManager;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.JavaCodeInjectionError;
import io.je.utilities.instances.ClassRepository;
import io.je.utilities.instances.InstanceManager;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;


public class Executioner {
    public static ObjectMapper objectMapper = new ObjectMapper();
    //private static final ExecutorService executor = Executors.newFixedThreadPool(10);


    private Executioner() {
    }


    /*************************************** INFORM *********************************************************/
    
    /*
     * Execute inform block 
     */
    public static void informRuleBlock(String projectId, String ruleId, String message, String logDate, String blockName) {
        try {
            new Thread(new Runnable() {


                @Override
                public void run() {

                    try {
                    	
                        JELogger.info(message, LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId,blockName);
                        //   JELogger.info(objectMapper.writeValueAsString(msg), LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JELogger.error(JEMessages.INFORM_BLOCK_ERROR, LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);

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
    public static void updateInstanceAttributeValueFromStaticValue(String projectId, String ruleId, String blockName,String instanceId, String attributeName, Object value) {
        //Rework to use a callable for exception handling

        try {
            new Thread(() -> {

                try{
                    //TODO: add blockName
                    InstanceManager.writeToDataModelInstance(instanceId,attributeName,value);

                }catch(Exception e)
                {
                    JELogger.error(JEMessages.WRITE_INSTANCE_FAILED + e.getMessage(),  LogCategory.RUNTIME,
                            projectId, LogSubModule.RULE, ruleId,blockName);
                }

            }).start();
        } catch (Exception e) {
        	JELogger.error(JEMessages.WRITE_INSTANCE_FAILED ,  LogCategory.RUNTIME,
        			projectId, LogSubModule.RULE, ruleId,blockName);
        }

    }

    /*****SET FROM VARIABLE*****/
    /*
     * update instance attribute from  variable
     */
    public static void updateInstanceAttributeValueFromVariable(String projectId,String instanceId,String attributeName ,String variableId) {

        try {
            new Thread(() -> {

                try {

                     Object attribueValue = VariableManager.getVariableValue(projectId, variableId);
                     InstanceManager.writeToDataModelInstance(instanceId,attributeName,attribueValue);
                } catch (Exception e) {
                    e.printStackTrace();

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
            new Thread(() -> {
                Object attribueValue = InstanceManager.getAttributeValue(sourceInstanceId, sourceAttributeName);
            	if(attribueValue==null)
                {
                    JELogger.error("Failed to read instance value", null, projectId, LogSubModule.RULE, sourceInstanceId);
                    return;
                }

                InstanceManager.writeToDataModelInstance(destinationInstanceId,destinationAttributeName,attribueValue);




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
            new Thread(() -> {

                try {
                   JERunnerAPIHandler.writeVariableValue(projectId, variableId, value);
                } catch (Exception e) {
                    e.printStackTrace();

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
            new Thread(() -> {

                try {
                    JERunnerAPIHandler.writeVariableValue(projectId, destinationVariableId, VariableManager.getVariableValue(projectId, sourceVariableId));
                } catch (Exception e) {
                    e.printStackTrace();

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
    public static void updateVariableValueFromDataModel(String projectId,String destinationVariableId, String sourceInstanceId, String sourceAttributeName) {

        try {
            new Thread(() -> {

                try {
                    Object attribueValue = InstanceManager.getAttributeValue(sourceInstanceId, sourceAttributeName);
                    JERunnerAPIHandler.writeVariableValue(projectId, destinationVariableId, attribueValue);

                } catch (Exception e) {
                    e.printStackTrace();

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
            new Thread(() -> {

                try {
                    JERunnerAPIHandler.triggerEvent(eventId, projectId);

                } catch (Exception e) {
                    e.printStackTrace();

                }


            }).start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }



   

    


    
   
    
   /* public static void main(String[] args) {
        executeScript("test", "", "");
    }*/
    public static void executeScript(String name, String processId, String projectId, int timeout) throws JavaCodeInjectionError, ClassNotFoundException {
       // JEClassLoader.overrideInstance(ClassBuilderConfig.generationPackageName +"." + name);
        Class<?> loadClass = null;
        try {
            loadClass = ClassRepository.getClassByName(name); /*JEClassLoader.getInstance().loadClass(ClassBuilderConfig.generationPackageName +"." + name);*/
            Method method = loadClass.getDeclaredMethods()[0];
            method.invoke(null);
        } catch (  InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        /*ExecutorService executor = Executors.newFixedThreadPool(1);

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
        //JobEngine.informUser("hello everyone, your uploaded file is " + JobEngine.getJarFile("org.eclipse.jdt.core-3.7.1.jar").getName(), "testId");
        catch(Exception e) {
            msg = "Script task in workflow with id = " + processId + " in project with id = " + projectId + " was interrupted during the execution";
            JELogger.error(msg, LogCategory.RUNTIME, projectId,
                    LogSubModule.JERUNNER, processId);
            task.cancel(true);
            throw new JavaCodeInjectionError(msg);
        }*/
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
    public static void main(String[] args) {

        Connection conn = null;

        try {

            String dbURL = "jdbc:sqlserver://YRIAHI-PC\\SQLEXPRESS:1433;databaseName=SIOTHDB;user=sa;password=io.123";
            String user = "sa";
            String pass = "io.123";
            File jarPath = new File("D:\\jars\\mssql-jdbc-8.4.1.jre8.jar");

            try {

                URLClassLoader child = new URLClassLoader(
                        new URL[] {jarPath.toURI().toURL()}

                );

                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver", true, child);
                ClassLoader loader = JobEngine.class.getClassLoader();
                child.loadClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Driver driver = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver", true, child).newInstance();
                conn = driver.connect(dbURL, null);

                if (conn != null) {

                    // if you only need a few columns, specify them by name instead of using "*"
                    String query = "SELECT * FROM Eqpt";

                    // create the java statement
                    Statement st = conn.createStatement();

                    // execute the query, and get a java resultset
                    ResultSet rs = st.executeQuery(query);

                    // iterate through the java resultset
                    while (rs.next())
                    {
                        int equipmentid = rs.getInt("equipmentid");
                        String equipmentname = rs.getString("equipmentname");


                        // print the results
                        System.out.print("The equipment number  ");
                        System.out.format("%s,%s,%s\n", equipmentid,"is",equipmentname);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
