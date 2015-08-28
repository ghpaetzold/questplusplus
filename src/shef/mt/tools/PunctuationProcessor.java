
package shef.mt.tools;

import java.util.HashMap;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Adds punctuation information to the sentences processed.
 * @author GustavoH
 */
public class PunctuationProcessor extends ResourceProcessor {

    private HashMap<String, Integer> punctuationMap;
    
    public PunctuationProcessor() {
        //Initialize punctuation map:
        this.punctuationMap = new HashMap<>();
        
        //Create list of punctuations:
        String[] punctuations = new String[]{",", ".", ";", ":", "'", "!", "?", "\""};
        for(String punctuation: punctuations){
            this.punctuationMap.put(punctuation, 1);
        }
    }

    @Override
    public void processNextSentence(Sentence source) {
        source.setValue("punctuation", this.punctuationMap);
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
