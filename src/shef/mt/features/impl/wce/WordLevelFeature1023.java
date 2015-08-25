package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1023 extends WordLevelFeature {

    public WordLevelFeature1023() {
        this.setIndex("WCE1023");
        this.setIdentifier("STOP");
        this.setDescription("1 if target word is a stop word, 0 if not.");
        this.addResource("target.stopwords");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get tokens from target sentence:
        String[] tokens = target.getTokens();
        
        //Get stop words hash:
        HashMap<String, Integer> stopWords = (HashMap<String, Integer>) target.getValue("stopwords");

        //For each token, check if it is a stop word:
        for (int i = 0; i < tokens.length; i++) {
            String value = this.getIdentifier() + '=';
            if(stopWords.get(tokens[i])!=null){
                value += 1;
            }else{
                value += 0;
            }
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }
}
