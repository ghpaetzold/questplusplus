package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1032 extends WordLevelFeature {

    public WordLevelFeature1032() {
        this.setIndex("WCE1032");
        this.setIdentifier("REPEAT");
        this.setDescription("Number of times each target word appears in the target sentence.");
        this.addResource("wordcounts");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Ge tokens from sentences:
        String[] targetTokens = target.getTokens();
        
        //Get alignments object:
        HashMap<String, Integer> counts = (HashMap<String, Integer>) target.getValue("wordcounts");

        //For each token, check if it has a dependency link:
        for (int i = 0; i < targetTokens.length; i++) {
            //Get index of aligned word in source sentence:
            Integer targetCount = counts.get(targetTokens[i]);
            
            //Save value:
            String value = this.getIdentifier() + '=' + targetCount;
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }
}
