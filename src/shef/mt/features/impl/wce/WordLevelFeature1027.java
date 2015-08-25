package shef.mt.features.impl.wce;

import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1027 extends WordLevelFeature {

    public WordLevelFeature1027() {
        this.setIndex("WCE1027");
        this.setIdentifier("NULLLINK");
        this.setDescription("1 if target word is linked to another, 0 if not.");
        this.addResource("target.parseModel");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get tokens from target sentence:
        String[] tokens = target.getTokens();
        
        //Get dependency counts for target tokens:
        HashMap<Integer, Integer> dependencyCounts = (HashMap<Integer, Integer>) target.getValue("depcounts");

        //For each token, check if it has a dependency link:
        for (int i = 0; i < tokens.length; i++) {
            String value = this.getIdentifier() + '=';
            
            if(dependencyCounts.get(i)!=null){
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
