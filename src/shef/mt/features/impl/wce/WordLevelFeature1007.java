package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1007 extends WordLevelFeature {

    public WordLevelFeature1007() {
        this.setIndex("WCE1007");
        this.setIdentifier("TRGCOUNT");
        this.setDescription("Target word count in sentence.");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get tokens from target sentence:
        String[] tokens = target.getTokens();

        //Count word occurrences:
        HashMap<String, Integer> frequencyCounts = new HashMap<>();
        for (int i = 0; i < tokens.length; i++) {
            if(frequencyCounts.get(tokens[i])!=null){
                frequencyCounts.put(tokens[i], frequencyCounts.get(tokens[i])+1);
            }else{
                frequencyCounts.put(tokens[i], 1);
            }
        }
        
        //Output word occurrences:
        for (int i = 0; i < tokens.length; i++) {
            String value = this.getIdentifier() + '=' + frequencyCounts.get(tokens[i]);
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }
}
