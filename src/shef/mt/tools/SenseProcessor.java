package shef.mt.tools;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lexvo.uwn.Entity;
import org.lexvo.uwn.Statement;
import org.lexvo.uwn.UWN;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Adds word sense information to the sentences processed.
 * @author GustavoH
 */
public class SenseProcessor extends ResourceProcessor {

    private UWN uwn;
    private String lang;

    public SenseProcessor(String path, String lang) {
        try {
            this.uwn = new UWN(new File(path));
            switch (lang) {
                case "english":
                    this.lang = "eng";
                    break;
                case "spanish":
                    this.lang = "spa";
                    break;
                case "german":
                    this.lang = "deu";
                    break;
                case "chinese":
                    this.lang = "cmn";
                    break;
            }
        } catch (Exception ex) {
            uwn = null;
            Logger.getLogger(SenseProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processNextSentence(Sentence s) {
        //Check if Universal Wordnet is properly allocated:
        if (uwn != null) {
            //Get sentence's tokens:
            String[] tokens = s.getTokens();

            //Initialize vector of sense counts:
            int[] senseCounts = new int[tokens.length];

            //Calculate sense counts for each token:
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                try {
                    Iterator<Statement> it = uwn.getMeanings(Entity.createTerm(token, this.lang));
                    int counter = 0;
                    while (it.hasNext()) {
                        it.next();
                        counter++;
                    }
                    senseCounts[i] = counter;
                } catch (IOException ex) {
                    senseCounts[i] = 0;
                    Logger.getLogger(SenseProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //Add sense counts as a resource:
            s.setValue("tools.universalwordnet.path", senseCounts);
        }else{
            System.out.println("ERROR: Universal Wordnet is not properly set. Check the path provided in the configuration file.");
        }
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
