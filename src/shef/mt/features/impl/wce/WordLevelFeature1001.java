package shef.mt.features.impl.wce;

import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1001 extends WordLevelFeature {

    public WordLevelFeature1001() {
        this.setIndex("WCE1001");
        this.setIdentifier("TRGWORD");
        this.setDescription("Target word.");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Ge tokens from target sentence:
        String[] tokens = target.getTokens();

        //For each token, create ngram:
        for (int i = 0; i < tokens.length; i++) {
            String value = this.getIdentifier() + '=' + tokens[i];
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }
}
