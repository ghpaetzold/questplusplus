package shef.mt.features.impl.wce;

import java.util.ArrayList;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1025 extends WordLevelFeature {

    public WordLevelFeature1025() {
        this.setIndex("WCE1025");
        this.setIdentifier("PROPERNOUN");
        this.setDescription("1 if target word is a proper noun, 0 if not.");
        this.addResource("target.POSModel");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];

        //Get tokens from target sentence:
        String[] tokens = target.getTokens();
        
        //Get stop words hash:
        ArrayList<String> targetPOSTags = (ArrayList<String>) target.getValue("postags");

        //For each token, check if it is a proper noun:
        for (int i = 0; i < tokens.length; i++) {
            String value = this.getIdentifier() + '=';
            
            String targetPOS = targetPOSTags.get(i);
            
            if(targetPOS.equals("NNP") || targetPOS.equals("np00000") || targetPOS.equals("NR") || targetPOS.equals("NE")){
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
