package utils.string;

import java.util.Random;
import java.util.UUID;

public class StringUtilities {

    public static String substring(String base, int start, int end) {
        return org.apache.commons.lang3.StringUtils.substring(base, start, end);
    }

    public static boolean isEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isEmpty(str);
    }

    public static String capitalize(String str) {
        return org.apache.commons.lang3.StringUtils.capitalize(str);
    }

    public static boolean isStringOnlyAlphabet(String str) {
        return (str != null) && ((!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }

    public static String generateRandomAlphabeticString(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        // create random string builder
        StringBuilder sb = new StringBuilder();
        // create an object of Random class
        Random random = new Random();
        for (int i = 0; i < length; i++) {

            // generate random index number
            int index = random.nextInt(alphabet.length());

            // get character specified by index
            // from the string
            char randomChar = alphabet.charAt(index);

            // append the character to string builder
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
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
