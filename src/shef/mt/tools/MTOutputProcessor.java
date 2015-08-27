/**
 *
 */
package shef.mt.tools;

import shef.mt.util.Logger;
import shef.mt.util.ExtensionFilter;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.Translation;
import shef.mt.features.util.Phrase;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import shef.mt.features.util.Doc;

/**
 * This class receives as input an XML file containing sentences and lists of
 * translation with various attributes and reads it into Sentence objects
 *
 * @author Catalina Hallett
 *
 */
public class MTOutputProcessor extends ResourceProcessor {

    private int sentCount;
    private Document doc;
    private NodeList sentences;
    private BufferedWriter nbestOut;
    private BufferedWriter sentOut;
    private File input;
    private String nbestPath;
    private NGramExec ngramExec;
    private int ngramSize;

    public MTOutputProcessor(String input, String nbestPath, String ngramPath, int ngramSize) {
        Logger.log("[MTOutputProcessor]");
        this.input = new File(input);
        this.nbestPath = nbestPath;
        this.ngramSize = ngramSize;
        initialiseXML();
        sentCount = 0;
        ngramExec = new NGramExec(ngramPath);
    }

    public void initialiseXML() {
        try {
            System.out.println("MTOutputProcessor");
            Logger.log("Initialising XML...");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(input);
            doc.getDocumentElement().normalize();
            sentences = doc.getElementsByTagName("Sentence");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processNextSentence(Sentence sent) {
        if (sentCount >= sentences.getLength()) {
            return;
        }
        try {
            String ngramSentFile = nbestPath + File.separator + input.getName().trim() + "_" + sent.getIndex() + ".nbest";
            String sentFile = nbestPath + File.separator + input.getName().trim() + "_" + sent.getIndex() + ".sent";

            nbestOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ngramSentFile), "utf-8"));
            sentOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sentFile), "utf-8"));

            Element sentNode = (Element) sentences.item(sentCount);
            NamedNodeMap attrs = sentNode.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Attr attr = (Attr) attrs.item(i);
                sent.setValue(attr.getName(), attr.getValue());
                ResourceManager.registerResource(attr.getName());
            }
            NodeList translations = sentNode.getElementsByTagName("translation");
            for (int i = 0; i < translations.getLength(); i++) {
                Element trans = (Element) translations.item(i);
                attrs = trans.getAttributes();
                Translation t = new Translation();
                for (int j = 0; j < attrs.getLength(); j++) {
                    Attr attr = (Attr) attrs.item(j);
                    String name = attr.getName();
                    ResourceManager.registerResource(name);
                    String value = attr.getValue();
                    if (name.equals("text")) {
                        t.setText(value);
                    } else if (name.equals("rank")) {
                        t.setRank(Integer.parseInt(value));
                    } else {
                        t.setAttribute(name, value);
                        //   					System.out.println(name+"="+value);
                    }
                }
                t.countTokens();
                sent.addTranslation(t);

                nbestOut.write(trans.getAttribute("text") + "\r\n");

            }
            sentOut.write(sent.getText());
            nbestOut.close();
            sentOut.close();
            for (int i = 1; i <= ngramSize; i++) {
                String sentLMFile = nbestPath + File.separator + i + "_" + input.getName().trim() + "_" + sent.getIndex() + ".lm";
                String pplFile = nbestPath + File.separator + i + "_" + input.getName().trim() + "_" + sent.getIndex() + ".ppl";
                ngramExec.runNGramCount(ngramSentFile, sentLMFile, ngramSize);
                ngramExec.runNGramPerplex(sentFile, pplFile, sentLMFile, i);
                PPLProcessor pplProcSource = new PPLProcessor(pplFile, new String[]{i + "_nb_logprob", i + "_nb_ppl", i + "_nb_ppl1"});
                pplProcSource.processNextSentence(sent);
                pplProcSource.close();
            }

            NodeList phrases = sentNode.getElementsByTagName("phrase");
            if (phrases != null && phrases.getLength() != 0) {
                ResourceManager.registerResource("phrases");
            }
            for (int i = 0; i < phrases.getLength(); i++) {
                Element phrase = (Element) phrases.item(i);
                Phrase p = new Phrase(phrase.getAttribute("text"), phrase.getAttribute("start"), phrase.getAttribute("end"));
                sent.addPhrase(p);
            }
            sent.setValue("nbestSize", translations.getLength());
            sent.buildSentenceModel();
            sentCount++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public void deleteTempFiles() {

        File f = new File(nbestPath);
        if (f.isDirectory()) {
            File[] files = f.listFiles(new ExtensionFilter(new String[]{".sent", ".nbest"}));
            for (File file : files) {
                file.delete();
            }
        }

    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
