
package shef.mt.features.impl.wce;

import java.util.ArrayList;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1016 extends WordLevelFeature{

    public WordLevelFeature1016() {
        this.setIndex("WCE1016");
        this.setIdentifier("TRGPOS");
        this.setDescription("POS of target word.");
        this.addResource("target.POSModel");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];
        
        //Get tokens from target sentence:
        String[] tokens = target.getTokens();
        
        //Get pos tags or target sentence:
        ArrayList<String> targetPOSTags = (ArrayList<String>) target.getValue("postags");
        
        //Get POS tags of each target word:
        for(int i=0; i<tokens.length; i++){
            String value = this.getIdentifier()+'='+targetPOSTags.get(i);
            result[i] = value;
        }
        
        //Save values produced:
        this.setValues(result);
    }
}
