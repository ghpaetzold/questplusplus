package shef.mt.xmlwrap;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;

import shef.mt.util.Logger;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

/**
 * This class produces an XML file from the output of the MOSES machine
 * translation system It also produces a series of text files from the nbest
 * list (one file per sentence), which will be used in computing the glass box
 * features that rely on ngrams computed on the nbest list
 *
 */
public class MOSES_XMLWrapper implements XMLWrapper {

    final static String featName[] = {
        "text",
        "dist_feat_1",
        "dist_feat_2",
        "dist_feat_3",
        "dist_feat_4",
        "dist_feat_5",
        "dist_feat_6",
        "dist_feat_7",
        "lm_features",
        "word_penalty_feature",
        "phrase_prob_feat_1",
        "phrase_prob_feat_2",
        "phrase_prob_feat_3",
        "phrase_prob_feat_4",
        "phrase_prob_feat_5",
        "prob"
    };
    private int nrFeatures = 16;
    private String inFile;
    private String outFile;
    private BufferedReader inputFileReader;
    private BufferedReader logFileReader;
    private BufferedReader phraseFileReader;
    private BufferedWriter outText;
    private Element root;
    private String sentIndex;
    private Element crtSent;
    private Document doc;
    private String phraseFile;
    private float bestScore;
    private Element bestTrans;
    private int count = 0;
    private BufferedWriter nbestOut;
    private String nbestPath;

    public MOSES_XMLWrapper(String inputFile, String outputFile, String phraseFile, String logFile) {
        inFile = inputFile;
        outFile = outputFile;
        bestScore = 0;
//		this.nbestPath = nbestPath;
        File f = new File(outFile);
        Logger.log("MOSES_XMLWrapper running ...");
        Logger.log("nbest input file: " + inputFile);
        Logger.log("1-best phrases file: " + phraseFile);

        if (f.exists()) {
            Logger.log("Output file " + outputFile + " already exists. MOSES XMLWrapper will not run.");
            System.out.println("Output file " + outputFile + " already exists. MOSES XMLWrapper will not run.");
        } else {
            try {
                outText = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inputFile + ".txt"), "utf-8"));
                inputFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "utf-8"));
                logFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "utf-8"));
                phraseFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(phraseFile), "utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            nrFeatures = featName.length;
            sentIndex = "";
            createXMLDoc();
        }
        registerResources();
    }

    public void registerResources() {
        for (String res : featName) {
            shef.mt.tools.ResourceManager.registerResource(res);
        }
    }

    public void setSentencePhrases(Element sent) {
        try {
            String phraseLine = phraseFileReader.readLine();
            if (phraseLine == null) {
                return;
            }
            String[] phrases = phraseLine.split("\\|");
            String phraseText;
            String start;
            String end;
//			System.out.println(phraseLine);
            for (int i = 0; i < phrases.length; i += 2) {
                Element phrase = doc.createElement("phrase");
                phrase.setAttribute("text", phrases[i].trim());
                String[] span = phrases[i + 1].split("-");
//				System.out.println(phrases[i+1]+" span length="+span.length);
                phrase.setAttribute("start", span[0].trim());
                phrase.setAttribute("end", span[1].trim());
                sent.appendChild(phrase);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log(e.getStackTrace().toString());
        }
    }

    public void parseLine(String line) {
        String[] features = line.split("\\|\\|\\|");
        if (features.length < 2) {
            return;
        }
        String index = features[0].trim();

        if (!index.equals(sentIndex)) //new sentence
        {
            //		System.out.println("new sentence "+sentIndex+" count="+count);
            //		System.out.println(line);
            sentIndex = index;
//			String source = getSentenceText();
            if (count != 0) {
                orderTranslations(crtSent);
            }
            crtSent = writeSentenceToXML();
            getSentenceFeatures(crtSent);
            count++;
        }
        String[] values = new String[nrFeatures + 2];
        values[0] = features[1];
        String crtSplitValue;
        float crtFloatValue;

        Scanner scan;
        int loop;
        int scanCount = 1;
        if (features.length == 6) // alignment infos are provided as extra fields
            loop = features.length - 2;
                    else 
            loop = features.length; // alignments are not provided
       // System.out.println(" feature length is = " +features.length);
        for (int i = 2; i < loop; i++) {
            crtSplitValue = features[i];
	//		System.out.println("crtSplitValue="+crtSplitValue);
            if (!crtSplitValue.equals("")) {
                scan = new Scanner(crtSplitValue);
                while (scan.hasNext()) {
                    while (!scan.hasNextFloat()) {
                        scan.next();
                    }
                    while (scan.hasNextFloat()) {
                        values[scanCount] = scan.nextFloat() + "";
                        scanCount++;
                    }
                }
            }
        }

        writeTranslationToXML(crtSent, values);
    }

    private String getSentenceText() {
        String line = null;
        try {
            line = inputFileReader.readLine().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;

    }

    private void getSentenceFeatures(Element sent) {
        try {
            String line = logFileReader.readLine().trim();

            while (!line.startsWith("total hypotheses") && line != null) {
                line = logFileReader.readLine();
            }
            //ugly: skip 13 characters representing Translating:

            String[] values = line.split("=");
            sent.setAttribute("totalHypotheses", values[1].trim());
            line = logFileReader.readLine();
            values = line.split("=");
            sent.setAttribute("notBuilt", values[1].trim());
            logFileReader.readLine();
            line = logFileReader.readLine();
            values = line.split("=");
            sent.setAttribute("discarded", values[1].trim());
            line = logFileReader.readLine();
            values = line.split("=");
            sent.setAttribute("recombined", values[1].trim());
            line = logFileReader.readLine();
            values = line.split("=");
            sent.setAttribute("pruned", values[1].trim());

            while (!line.startsWith("BEST TRANSLATION") && line != null) {
                line = logFileReader.readLine();
            }

            values = line.split("\\|");
            sent.setAttribute("unknown", String.valueOf(values.length - 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createXMLDoc() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            doc = impl.createDocument(null, null, null);
            root = doc.createElement("text");
            doc.appendChild(root);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Element writeSentenceToXML() {
        Element sent = doc.createElement("Sentence");
        sent.setAttribute("index", sentIndex);
//		sent.setAttribute("source", source);
        setSentencePhrases(sent);
        root.appendChild(sent);
        return sent;
    }

    private void writeTranslationToXML(Element sent, String[] values) {
        try {
            Element trans = doc.createElement("translation");
            //   if (values[0])

            for (int i = 0; i < nrFeatures; i++) {
                trans.setAttribute(featName[i], values[i]);
            }

            trans.setAttribute("rank", "");
            sent.appendChild(trans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeXML() {
        try {
            // transform the Document into a String
            DOMSource domSource = new DOMSource(doc);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            //transformer.setOutputProperty
//          (OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            File f = new File(outFile);
            StreamResult sr = new StreamResult(f);
            transformer.transform(domSource, sr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {
        File f = new File(outFile);
        if (f.exists()) {
            Logger.log("Output file " + outFile + " already exists. MOSES XMLWrapper will not run.");
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "utf-8"));
            String line = br.readLine();
            while (line != null) {
                parseLine(line);
                line = br.readLine();
            }
            orderTranslations(crtSent);
            /*		for (int i=0; i<count;i++){
             String sentFile = nbestPath+File.separator+(new File(inFile)).getName().trim()+"_"+i+".nbest";
             }*/
            br.close();
            outText.close();
            logFileReader.close();
            if (nbestOut != null) {
                nbestOut.close();
            }
            writeXML();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.log("MOSES XMLWrapper done!");
    }

    private void orderTranslations(Element sent) {
        NodeList translations = sent.getElementsByTagName("translation");
        java.util.TreeSet<Element> trans = new TreeSet<Element>(new TranslationElementComparator());
        for (int i = 0; i < translations.getLength(); i++) {
            trans.add((Element) translations.item(i));
        }
        Iterator<Element> it = trans.iterator();
        Element el;
        int pos = 0;
        while (it.hasNext()) {
            el = it.next();
            el.setAttribute("rank", String.valueOf(pos));
            pos++;
        }
        try {
            outText.write(trans.first().getAttribute("text") + "\r\n");
//		System.out.println(trans.first().getAttribute("text"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        MOSES_XMLWrapper cmu = new MOSES_XMLWrapper(args[0], args[1], args[2], args[3]);
        cmu.run();

    }
}
