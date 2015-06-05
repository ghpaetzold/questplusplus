/**
 *
 */
package shef.mt.syntree;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;

import shef.mt.util.Logger;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

/**
 * Given 2 files in TigerXML format representing parallel trees, this class
 * produces a model file where sentences are aligned at root level
 *
 * @author Catalina Hallett
 *
 *
 */
public class TreeAlignModel {

    private String xmlSource;
    private String xmlTarget;
    private Document doc;
    private Element root;
    private NodeList sentences;
    private String model;

    public TreeAlignModel(String xmlSource, String xmlTarget, String model) {
        this.xmlSource = xmlSource;
        this.xmlTarget = xmlTarget;
        this.model = model;
    }

    public void run() {
        createXMLDoc();
        readXML();
        writeXML();
    }

    public void readXML() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlSource);
            doc.getDocumentElement().normalize();
            NodeList roots = doc.getElementsByTagName("graph");
            for (int i = 0; i < roots.getLength(); i++) {
                Element root = (Element) roots.item(i);
                System.out.println(root.getNodeName() + " " + root.getAttribute("root"));
                addAlignment(root.getAttribute("root"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     <alignments>
     <align type="good" last_change="2010-03-29" author="Gideon">
     <node treebank_id="en" node_id="s5_501"/>
     <node treebank_id="nl" node_id="s10_0"/>
     </align>
     */
    private void createXMLDoc() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            doc = impl.createDocument(null, null, null);
            root = doc.createElement("alignments");
            doc.appendChild(root);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addAlignment(String id) {
        Element elem = doc.createElement("align");
        elem.setAttribute("type", "good");
        elem.setAttribute("author", "cat");
        Element nodeSource = doc.createElement("node");
        Element nodeTarget = doc.createElement("node");
        nodeSource.setAttribute("treebank_id", "ar");
        nodeSource.setAttribute("node_id", id);
        nodeTarget.setAttribute("treebank_id", "en");
        nodeTarget.setAttribute("node_id", id);
        elem.appendChild(nodeSource);
        elem.appendChild(nodeTarget);
        root.appendChild(elem);
    }

    private void writeXML() {
        try {
            // transform the Document into a String
            DOMSource domSource = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            //transformer.setOutputProperty
//			          (OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            File f = new File(model);
            StreamResult sr = new StreamResult(f);
            transformer.transform(domSource, sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        TreeAlignModel tam = new TreeAlignModel(args[0], "", args[1]);
        tam.run();
    }
}
