
package shef.mt.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Adds stop word information to the sentences processed.
 * @author GustavoH
 */
public class StopWordsProcessor extends ResourceProcessor {

    private HashMap<String, Integer> stopWords;

    public StopWordsProcessor(String path) {
        //Create hash of words:
        this.stopWords = new HashMap<>();
        
        //Read and store words from file:
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            while(reader.ready()){
                String word = reader.readLine().trim();
                this.stopWords.put(word, 1);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StopWordsProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StopWordsProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void processNextSentence(Sentence s) {
        s.setValue("stopwords", this.stopWords);
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
