/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.util;

import java.io.*;
import java.util.*;

/**
 *
 * @author cat
 */
public class PropertiesManager extends java.util.Properties {

    private static boolean notFound = false;
    private static String defConfigFile = "config.properties";
    private static final boolean THROW_EXCEPTION_ON_LOAD_FAILURE = true;
    private static final boolean LOAD_AS_RESOURCE_BUNDLE = false;
    private static final String SUFFIX = ".properties";

    public PropertiesManager() {
        String config = findConfig();
        if (config != null) {
            try {
                System.out.println("Loading properties from file: " + config);
                File f = new File(config);
                //  System.out.println(f.getPath());
                FileInputStream in = new FileInputStream(config);
                this.load(in);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();;
            }
        } else {
            System.out.println("Can't find default config file");
        }
    }

    public PropertiesManager(String file) {
        try {
            File f = new File(file);
            //  System.out.println(f.getPath());
            FileInputStream in = new FileInputStream(file);
            this.load(in);
            in.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            notFound = true;
        }
    }

    private void loadProperties(String name, ClassLoader loader) {
        if (name == null) {
            throw new IllegalArgumentException("null input: name");
        }

        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        if (name.endsWith(SUFFIX)) {
            name = name.substring(0, name.length() - SUFFIX.length());
        }

        Properties result = null;

        InputStream in = null;
        try {
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }

            if (LOAD_AS_RESOURCE_BUNDLE) {
                name = name.replace('/', '.');
// Throws MissingResourceException on lookup failures:
                final ResourceBundle rb = ResourceBundle.getBundle(name,
                        Locale.getDefault(), loader);

                result = new Properties();
                for (Enumeration keys = rb.getKeys(); keys.hasMoreElements();) {
                    final String key = (String) keys.nextElement();
                    final String value = rb.getString(key);
                    result.put(key, value);
                }
            } else {
                name = name.replace('.', '/');

                if (!name.endsWith(SUFFIX)) {
                    name = name.concat(SUFFIX);
                }

// Returns null on lookup failures:
                in = loader.getResourceAsStream(name);
                if (in != null) {

                    this.load(in); // Can throw IOException
                }
            }
        } catch (Exception e) {
            notFound = true;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable ignore) {
                }
            }
        }

        if (THROW_EXCEPTION_ON_LOAD_FAILURE && (this == null)) {
            throw new IllegalArgumentException("could not load [" + name + "]"
                    + " as " + (LOAD_AS_RESOURCE_BUNDLE
                    ? "a resource bundle"
                    : "a classloader resource"));
        }

    }

//
    public HashMap<String, String> getProperties(String suff) {
        String suffix = suff.toLowerCase() + ".";
        //  System.out.println(suffix);
        HashMap<String, String> result = new HashMap<String, String>();
        Enumeration keys = propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            //    System.out.println(key);
            if (key.startsWith(suffix)) {
                //       System.out.println(key);
                result.put(key, getProperty(key));
            }
        }
        return result;
    }

    public void loadProperties(final String name) {
        loadProperties(name, Thread.currentThread().getContextClassLoader());
    }

    public String getString(String key) {
        return (String) (get(key));
    }

    private static String findConfig() {
        String config = defConfigFile;
        File f = new File(config);
        if (f.exists()) {
            return config;
        }
        //search outside the package
        try {
            f = new File(shef.mt.util.PropertiesManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
//        config = "./../../../"+defConfigFile;

            f = new File(f.getPath() + File.separator + config);
            System.out.println("looking in " + f.getPath());
            if (f.exists()) {
                return f.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //search user.dir
        f = new File(System.getProperty("user.dir") + File.separator + defConfigFile);
        System.out.println("looking in " + f.getPath());
        if (f.exists()) {
            return f.getPath();
        }

        //serach classpath
//        f = new File(System.getProperty("java.class.path")
        return null;
    }
}
