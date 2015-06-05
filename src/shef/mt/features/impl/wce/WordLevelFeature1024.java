package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1024 extends WordLevelFeature {

    public WordLevelFeature1024() {
        this.setIndex("WCE1024");
        this.setIdentifier("PUNCT");
        this.setDescription("1 if target word is punctuation, 0 if not.");
        this.addResource("punctuation");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get tokens from target sentence:
        String[] tokens = target.getTokens();
        
        //Get stop words hash:
        HashMap<String, Integer> punctuations = (HashMap<String, Integer>) target.getValue("punctuation");

        //For each token, check if it is punctuation:
        for (int i = 0; i < tokens.length; i++) {
            String value = this.getIdentifier() + '=';
            if(punctuations.get(tokens[i])!=null){
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
