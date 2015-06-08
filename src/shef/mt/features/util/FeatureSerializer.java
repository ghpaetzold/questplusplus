/**
 *
 */
package shef.mt.features.util;

import shef.mt.features.impl.Feature;
import java.io.*;
import java.net.URL;
import java.util.*;
import shef.mt.util.Logger;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

/**
 * @author Catalina Hallett
 *
 */
public class FeatureSerializer {

    String xml;
    String path;
    int mode;
    Document doc;
    Element root;

    /**
     *
     * @param path the package containing the features
     * @param xml the path to the xml file
     * @param mode boolean value indicating whether the features will be
     * appended to the xml file (true) or if the file will be re-written (false)
     * Use "false", for example, when all Feature classes are in the same
     * package, or when you are only interested in the Features contained in a
     * certain package<br> Use "true" in most cases where you have implemented
     * new features in a new package and want to add them to already defined
     * features
     *
     */
    public FeatureSerializer(String path, String xml, int mode) {
        this.xml = xml;
        this.path = path;
        this.mode = mode;
        createXMLDoc();
        run();
        writeXML();
    }

    private void createXMLDoc() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            if (mode == 0) {
                doc = builder.parse(xml);
                doc.getDocumentElement().normalize();
                root = (Element) doc.getElementsByTagName("features").item(0);
            } else {
                doc = impl.createDocument(null, null, null);
                root = doc.createElement("features");
                doc.appendChild(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * runs the feature serializer
     */
    private void run() {
        try {
            Class[] features = getClasses(path);
            //we'll double-check that the classes extend wlv.mt.features.impl.Feature
            Class superClass = Class.forName("wlv.mt.features.impl.Feature");
            Feature f;
            for (Class c : features) {
                if (c.getSuperclass().equals(superClass)) {
                    f = (Feature) c.newInstance();
                    writeFeatureToXML(f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base
     * directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    private void writeFeatureToXML(Feature feat) {
        Element feature = doc.createElement("feature");
        feature.setAttribute("index", feat.getIndex());
        feature.setAttribute("class", feat.getClass().getName());
        feature.setAttribute("description", feat.getDescription());
        root.appendChild(feature);
    }

    private void writeXML() {
        try {
            // transform the Doc into a String
            DOMSource domSource = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            //transformer.setOutputProperty
//		          (OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            File f = new File(xml);
            StreamResult sr = new StreamResult(f);
            transformer.transform(domSource, sr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new FeatureSerializer(args[0], args[1], new Integer(args[2]).intValue());
    }
}
