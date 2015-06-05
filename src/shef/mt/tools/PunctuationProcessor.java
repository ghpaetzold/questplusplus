
package shef.mt.tools;

import java.util.HashMap;
import shef.mt.features.util.Sentence;

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

}
