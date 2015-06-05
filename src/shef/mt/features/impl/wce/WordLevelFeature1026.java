package shef.mt.features.impl.wce;

import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1026 extends WordLevelFeature {

    public WordLevelFeature1026() {
        this.setIndex("WCE1026");
        this.setIdentifier("NUMERAL");
        this.setDescription("1 if target word is a numeral, 0 if not.");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get tokens from target sentence:
        String[] tokens = target.getTokens();

        //For each token, check if it is a numeral:
        for (int i = 0; i < tokens.length; i++) {
            String value = this.getIdentifier() + '=';
            
            try{
                double numeral = Double.parseDouble(tokens[i]);
                value += 1;
            }catch(Exception ex){
                value += 0;
            }
            
            result[i] = value;
        }

        //Save values produced:
        this.setValues(result);
    }
}
