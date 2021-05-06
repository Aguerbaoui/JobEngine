package io.je.utilities.string;

import io.je.utilities.logger.JELogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JEStringUtils {

    public static String substring(String base, int start, int end) {
        return StringUtils.substring(base, start, end);
    }

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }
    
    public static String capitalize(String str)
    {
    	return StringUtils.capitalize(str);
    }

    public static boolean isStringOnlyAlphabet(String str) {
        return (str != null) &&((!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }


    /*public static void  main(String[] args) {
        File f = new File("D:\\commons-lang3-3.11.jar");
        Set<String> classNames = new HashSet<>();
        Class testClass = null;
        try {
            try (JarFile jarFile = new JarFile(f)) {
                Enumeration<JarEntry> e = jarFile.entries();
                while (e.hasMoreElements()) {
                    JarEntry jarEntry = e.nextElement();
                    if (jarEntry.getName().endsWith(".class")) {
                        String className = jarEntry.getName()
                                .replace("/", ".")
                                .replace(".class", "");
                        //System.out.println(className + "\n");
                        classNames.add(className);
                    }
                }

            }
            Set<Class> classes = new HashSet<>(classNames.size());
            try (URLClassLoader cl = URLClassLoader.newInstance(
                    new URL[] { new URL("jar:file:" + f + "!/") })) {
                for (String name : classNames) {
                    Class clazz = cl.loadClass(name);
                    if(name.equals("org.apache.commons.lang3.StringUtils")) {
                        testClass = clazz;
                    }// Load the class by its name
                    classes.add(clazz);
                }
            }
            Method method = testClass.getMethod("isEmpty", CharSequence.class);
            JELogger.info("Response of jar file = " + method.invoke(null, new String("testStringToCapitalize")).toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        //commons-lang3-3.11
    }*/



}
