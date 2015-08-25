package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1029 extends WordLevelFeature {

    public WordLevelFeature1029() {
        this.setIndex("WCE1029");
        this.setIdentifier("PSEUDOREF");
        this.setDescription("1 if target word is in reference translation, 0 if not.");
        this.addResource("target.refTranslations");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get tokens from target sentence:
        String[] tokens = target.getTokens();
        
        //Get sense counts for target tokens:
        HashMap<String, Integer> refTranslationCounts = (HashMap<String, Integer>) target.getValue("reftranslation");

        //For each token, check if it has a dependency link:
        for (int i = 0; i < tokens.length; i++) {
            String value = this.getIdentifier() + '=';
            
            //Check if token is in keys of translation counts map:
            if(refTranslationCounts.get(tokens[i])!=null){
                value += '1';
            }else{
                value += '0';
            }
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }
}
