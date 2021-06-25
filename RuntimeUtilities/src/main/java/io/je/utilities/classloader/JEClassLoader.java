package io.je.utilities.classloader;

import io.je.utilities.logger.JELogger;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JEClassLoader extends ClassLoader {

    HashMap<String, InputStream> streams  = new HashMap<>();
    static Set<String> customClasses  ;

    
    static JEClassLoader instance;
    
    
    private JEClassLoader(Set<String> customClasses) {
        super(JEClassLoader.class.getClassLoader());
       this.customClasses=customClasses;
    }
    
    
    
    public static JEClassLoader getInstance()
    {
    	if(instance==null)
    	{
    		instance = new JEClassLoader( new HashSet<String>());
    		
    	}
    	return instance;
    }
    
    
    public static JEClassLoader overrideInstance()
    {
    	instance = new JEClassLoader(customClasses);
    	return instance;
    }
    
    
    private void loadAllClasses() throws ClassNotFoundException
    {
    	
    	for(String _class : customClasses)
    	{
    		try {
                Class c = getClass(_class);
              
            }catch (Exception e) {
               
             }
    	}
    }
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if(customClasses.contains(name))
        {
        	customClasses.remove(name);
        	loadAllClasses();
        }
        if (name.startsWith("jeclasses.") && !name.contains("Propagation")) {
            customClasses.add(name);

            try {
                JELogger.debug("Class Loading by je custom loader Started for " + name);
                Class c = getClass(name);
                return c;
            }
            catch (Exception e) {
              //  JELogger.debug("Class Loading failed by je custom loader for " + name);
               // JELogger.error(Arrays.toString(e.getStackTrace()));
            }
        }
        //JELogger.debug("Class Loading Started for " + name);
        return super.loadClass(name);

    }

    /**
     * Loading of class from .class file
     * happens here You Can modify logic of
     * this method to load Class
     * from Network or any other source
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> getClass(String name) throws ClassNotFoundException {
        String file = name.replace('.', File.separatorChar) + ".class";
        JELogger.debug("Name of File to be loaded in by class loader" + file);
        byte[] byteArr = null;
        try {
            // This loads the byte code data from the file
            byteArr = loadClassData(file);
            JELogger.debug("Size of byte array for the class "+byteArr.length);
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

    //needed for drools
    @Override
    public InputStream getResourceAsStream(final String name) {
        return streams.get(name);
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, Exception {

      //  JEClassLoader loader = new JEClassLoader(
        //        JEClassLoader.class.getClassLoader());

       // System.out.println("loader name---- " +loader.getParent().getClass().getName());

        //This Loads the Class we must always
        //provide binary name of the class
      //  Class<?> clazz =
          //      loader.loadClass("classes.testScripttScriptt");

      //  System.out.println("Loaded class name: " + clazz.getName());

        //Create instance Of the Class and invoke the particular method
        //Object instance = clazz.newInstance();

        //clazz.getMethod("printMyName").invoke(instance);
    }

}
