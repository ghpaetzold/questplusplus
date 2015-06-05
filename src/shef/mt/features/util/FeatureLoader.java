package shef.mt.features.util;

import shef.mt.features.impl.Feature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.*;

import org.w3c.dom.*;

/**
 * A FeatureLoader is responisble for instantiating Feature objects from a list
 * of feature indeces
 *
 * @author Catalina Hallett
 *
 */
public class FeatureLoader {

    String xmlFile;
    Document doc;
    HashMap<String, Feature> nodes;

    /**
     * instantiates the FeatureLoader from an XML feature configuration file
     *
     * @param xmlFile
     */
    public FeatureLoader(String xmlFile) {
        this.xmlFile = xmlFile;
        loadFeatures();
        System.out.println("features:" + nodes);
    }

    /**
     * Instantiates a Feature object from the XML Element that describes it
     *
     * @param node an XML Element that describe a Feature
     * @return a new Feature object instantiated from node
     */
    public Feature createFeature(Element node) {
        String className = node.getAttribute("class");
        String index = node.getAttribute("index");
        String description = node.getAttribute("description");
        Feature f = null;
        //	System.out.println("Creating feature "+className);
        try {
            Class c = Class.forName(className);
            f = (Feature) c.newInstance();
            f.setIndex(index);
            f.setDescription(description);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //	System.out.println("Feature created "+f);
        return f;
    }

    /**
     * Returns a Feature by index
     *
     * @param index
     * @return
     */
    public Feature getFeature(String index) {
        //System.out.println("retrieving index "+index +" from "+nodes);
        return nodes.get(index);
    }

    /**
     * Returns all features
     *
     * @return
     */
    public HashMap<String, Feature> getAllFeatures() {
        return nodes;
    }

    /**
     * Creates a list of features by inspecting the XML feature configuration
     * file and creating a Feature object from each <feature> node
     */
    public void loadFeatures() {
        nodes = new HashMap<String, Feature>();
        try {

            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(xmlFile));
            NodeList links = doc.getElementsByTagName("feature");

            for (int i = 0; i < links.getLength(); i++) {
                // for every link tag
                Element link = (Element) links.item(i);
                String index = link.getAttribute("index");
                nodes.put(index, createFeature(link));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
