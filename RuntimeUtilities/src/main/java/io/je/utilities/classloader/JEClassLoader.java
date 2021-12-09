package io.je.utilities.classloader;

import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.instances.ClassRepository;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class JEClassLoader extends ClassLoader {

    HashMap<String, InputStream> streams = new HashMap<>();

    static Set<String> dataModelCustomClasses;

    static Set<String> jeCustomClasses;

    static JEClassLoader dataModelInstance;

    static JEClassLoader jeInstance;

    private JEClassLoader(Set<String> jeCustomClasses, Set<String> dataModelCustomClasses) {
        super(Thread.currentThread().getContextClassLoader());
        if (jeCustomClasses != null) {
            JEClassLoader.jeCustomClasses = jeCustomClasses;
        }
        if (dataModelCustomClasses != null) {
            JEClassLoader.dataModelCustomClasses = dataModelCustomClasses;
        }
    }

    public static JEClassLoader getDataModelInstance() {
        if (dataModelInstance == null) {
            dataModelInstance = new JEClassLoader(null, new HashSet<>());

        }
        return dataModelInstance;
    }

    public static JEClassLoader getJeInstance() {
        if (jeInstance == null) {
            jeInstance = new JEClassLoader(new HashSet<>(), null);

        }
        return jeInstance;
    }

    public static JEClassLoader overrideDataModelInstance() {
        if (dataModelCustomClasses == null) {
            dataModelCustomClasses = new HashSet<>();
        }
        dataModelInstance = new JEClassLoader(null, dataModelCustomClasses);
        return dataModelInstance;
    }

    public static JEClassLoader overrideJeInstance() {
        if (jeCustomClasses == null) {
            jeCustomClasses = new HashSet<>();
        }
        jeInstance = new JEClassLoader(jeCustomClasses, null);
        return jeInstance;
    }

    public static JEClassLoader overrideDataModelInstance(String newClass) throws ClassNotFoundException {

        if (dataModelCustomClasses == null) {
            dataModelCustomClasses = new HashSet<>();
        }
        synchronized (dataModelCustomClasses) {
            dataModelInstance = new JEClassLoader(null, dataModelCustomClasses);
            dataModelCustomClasses.remove(newClass);
            Set<String> all = dataModelCustomClasses;
            for (String c : all) {
                ClassRepository.addClass(ClassRepository.getClassIdByName(c), c, dataModelInstance.loadClassInDataModelClassLoader(c));
            }

        }

        return dataModelInstance;
    }

    public static JEClassLoader overrideJeInstance(String newClass) throws ClassNotFoundException {

        if (jeCustomClasses == null) {
            jeCustomClasses = new HashSet<>();
        }
        synchronized (jeCustomClasses) {
            jeInstance = new JEClassLoader(jeCustomClasses, null);
            jeCustomClasses.remove(newClass);
            Set<String> all = jeCustomClasses;
            for (String c : all) {
                ClassRepository.addClass(ClassRepository.getClassIdByName(c), c, jeInstance.loadClassInJobEngineClassLoader(c));
            }

        }

        return jeInstance;
    }


    public Class<?> loadClassInJobEngineClassLoader(String className) throws ClassNotFoundException {
        if (className.startsWith(ClassBuilderConfig.generationPackageName + ".")
                && !className.contains("Propagation")) {
            jeCustomClasses.add(className);
            try {
                JELogger.trace("Class Loading by je custom loader Started for " + className, LogCategory.RUNTIME,
                        null, LogSubModule.CLASS, null);
                return jeInstance.getClass(className);
            } catch (Exception e) {
                // JELogger.debug("Class Loading failed by je custom loader for " + name);
                JELogger.error(Arrays.toString(e.getStackTrace()));
            }
        }
        return jeInstance.loadClass(className);
    }

    public Class<?> loadClassInDataModelClassLoader(String className) throws ClassNotFoundException {
        if (className.startsWith(ClassBuilderConfig.generationPackageName + ".")
                && !className.contains("Propagation")) {
            dataModelCustomClasses.add(className);
            try {
                JELogger.trace("Class Loading by dm custom loader Started for " + className, LogCategory.RUNTIME,
                        null, LogSubModule.CLASS, null);
                Class c = dataModelInstance.getClass(className);
                return c;
            } catch (Exception e) {
                // JELogger.debug("Class Loading failed by je custom loader for " + name);
                JELogger.error(Arrays.toString(e.getStackTrace()));
            }
        }

        return dataModelInstance.loadClass(className);

    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
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
        String file = name.replace('.', File.separatorChar) + ".class";
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

        InputStream stream = getClass().getClassLoader().getResourceAsStream(
                name);
        int size = stream.available();
        streams.put(name, stream);
        byte buff[] = new byte[size];
        DataInputStream in = new DataInputStream(stream);
        // Reading the binary data
        in.readFully(buff);
        in.close();
        return buff;
    }

    // needed for drools
    @Override
    public InputStream getResourceAsStream(final String name) {
        return streams.get(name);
    }

    /*
     * public static void main(String[] args) throws ClassNotFoundException,
     * InstantiationException, IllegalAccessException, IllegalArgumentException,
     * InvocationTargetException, NoSuchMethodException, Exception {
     * 
     * // JEClassLoader loader = new JEClassLoader(
     * // JEClassLoader.class.getClassLoader());
     * 
     * // System.out.println("loader name---- "
     * +loader.getParent().getClass().getName());
     * 
     * //This Loads the Class we must always
     * //provide binary name of the class
     * // Class<?> clazz =
     * // loader.loadClass("classes.testScripttScriptt");
     * 
     * // System.out.println("Loaded class name: " + clazz.getName());
     * 
     * //Create instance Of the Class and invoke the particular method
     * //Object instance = clazz.newInstance();
     * 
     * //clazz.getMethod("printMyName").invoke(instance);
     * }
     */

}
