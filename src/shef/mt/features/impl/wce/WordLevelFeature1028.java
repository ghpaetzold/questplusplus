package shef.mt.features.impl.wce;

import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1028 extends WordLevelFeature {

    public WordLevelFeature1028() {
        this.setIndex("WCE1028");
        this.setIdentifier("SENSECOUNT");
        this.setDescription("Number of senses of each target word.");
        this.addResource("tools.universalwordnet.path");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get tokens from target sentence:
        String[] tokens = target.getTokens();
        
        //Get sense counts for target tokens:
        int[] senseCounts = (int[]) target.getValue("tools.universalwordnet.path");

        //For each token, check if it has a dependency link:
        for (int i = 0; i < tokens.length; i++) {
            String value = this.getIdentifier() + '=' + senseCounts[i];
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }
}
