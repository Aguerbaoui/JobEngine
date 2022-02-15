package io.je.utilities.classloader;

import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.instances.ClassRepository;
import io.je.utilities.log.JELogger;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.string.StringUtilities;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import static io.je.utilities.config.ConfigurationConstants.JAVA_GENERATION_PATH;

public class JEClassLoader extends ClassLoader {

    HashMap<String, byte[]> streams = new HashMap<>();

    static Set<String> dataModelCustomClasses;

    static JEClassLoader dataModelInstance;

    static JEClassLoader currentRuleEngineClassLoader;

    private JEClassLoader(Set<String> dataModelCustomClasses) {
        super(Thread.currentThread().getContextClassLoader());
        if (dataModelCustomClasses != null) {
            JEClassLoader.dataModelCustomClasses = dataModelCustomClasses;
        }
    }

    public static JEClassLoader getDataModelInstance() {
        if (dataModelInstance == null) {
            dataModelInstance = new JEClassLoader(new HashSet<>());

        }
        return dataModelInstance;
    }

    public static JEClassLoader overrideDataModelInstance() {
        if (dataModelCustomClasses == null) {
            dataModelCustomClasses = new HashSet<>();
        }
        dataModelInstance = new JEClassLoader(dataModelCustomClasses);
        return dataModelInstance;
    }


    public static JEClassLoader overrideDataModelInstance(String newClass) throws ClassNotFoundException {

        if (dataModelCustomClasses == null) {
            dataModelCustomClasses = new HashSet<>();
        }
        synchronized (dataModelCustomClasses) {
            dataModelInstance = new JEClassLoader(dataModelCustomClasses);
            dataModelCustomClasses.remove(newClass);
            Set<String> all = dataModelCustomClasses;
            for (String c : all) {
                ClassRepository.addClass(ClassRepository.getClassIdByName(c), c, dataModelInstance.loadClass(c));
            }

        }

        return dataModelInstance;
    }


    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
       try {
    	   if (className.contains(ClassBuilderConfig.CLASS_PACKAGE + ".") //locondition hédhi classpackage = jeclasses donc y3adi féhom kol lénna
                   && !className.contains("Propagation")) {
               dataModelCustomClasses.add(className);
               try {
                   JELogger.trace("Class Loading by dm custom loader Started for " + className, LogCategory.RUNTIME,
                           null, LogSubModule.CLASS, null);
                   Class<?> c = dataModelInstance.getClass(className);
                   if(c == null) return super.loadClass(className);
                   return c;
               } catch (Exception e) {
                   //JELogger.debug("Class Loading failed by je custom loader for " + className);
                   //e.printStackTrace();
            	   //JELogger.error(Arrays.toString(e.getStackTrace()));
               }
           }
           return super.loadClass(className);
       }catch(Exception e){
       }
       return super.loadClass(className);
    }

    /**
     * Loading of class from .class file
     * happens here You Can modify logic of
     * this method to load Class
     * from Network or any other source
     * 
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> getClass(String name) throws ClassNotFoundException {
        String file = FileUtilities.getPathPrefix(JAVA_GENERATION_PATH) + name.replace('.', File.separatorChar) + ".class";
        if(!FileUtilities.fileExists(file)) return null; // zid method file exists to test with fibéli zédtha éna but its not here ahh dhaharli mazélt 3andi
        byte[] byteArr = null;
        try {
            // This loads the byte code data from the file
            byteArr = loadClassData(file);
            JELogger.trace("Size of byte array for the class " + byteArr.length, LogCategory.RUNTIME,
                    null, LogSubModule.CLASS, null);
            Class<?> c = defineClass(name, byteArr, 0, byteArr.length);
            resolveClass(c);
            return c;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads a given file and converts
     * it into a Byte Array
     * 
     * @param name
     * @return
     * @throws IOException
     */
    private byte[] loadClassData(String name) throws IOException {

        InputStream stream = new FileInputStream(name);
        String streamName = name.replace(FileUtilities.getPathPrefix(name), "").replace("\\",".");
        int size = stream.available();

        byte buff[] = new byte[size];
        DataInputStream in = new DataInputStream(stream);
        // Reading the binary data
        in.readFully(buff);
        in.close();
        streams.put(streamName, buff);
        return buff;
    }

    // needed for drools
    @Override
    public InputStream getResourceAsStream(final String name) {
        if(streams.containsKey(name)) {
            InputStream targetStream = new ByteArrayInputStream(streams.get(name));
            return targetStream;
        }
        
            return super.getResourceAsStream(name);
       
    }

	public static JEClassLoader getCurrentRuleEngineClassLoader() {
		return currentRuleEngineClassLoader;
	}

	public static void setCurrentRuleEngineClassLoader(JEClassLoader currentRuleEngineClassLoader) {
		JEClassLoader.currentRuleEngineClassLoader = currentRuleEngineClassLoader;
	}


    public static String getJobEnginePackageName(String packageName) {
        String imp = ConfigurationConstants.JAVA_GENERATION_PATH.replace(FileUtilities.getPathPrefix(ConfigurationConstants.JAVA_GENERATION_PATH), "");
        imp = imp.replace("\\", ".");
        imp = imp.replace("//", ".");
        imp = imp.replace("/", ".");
        if(StringUtilities.isEmpty(imp)) {
            imp = packageName;
        }
        else {
            imp = imp + "." + packageName;
        }
        return imp.replace("..", ".");
    }
}
