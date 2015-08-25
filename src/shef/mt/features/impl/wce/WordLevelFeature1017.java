
package shef.mt.features.impl.wce;

import java.util.ArrayList;
import java.util.HashMap;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.features.util.Sentence;

public class WordLevelFeature1017 extends WordLevelFeature{

    public WordLevelFeature1017() {
        this.setIndex("WCE1017");
        this.setIdentifier("SRCPOS");
        this.setDescription("POS of source word.");
        this.addResource("alignments.file");
        this.addResource("source.POSModel");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        //Create vector of resulting values:
        String[] result = new String[target.getNoTokens()];
        
        //Get tokens from target sentence:
        String[] targetTokens = target.getTokens();
        String[] sourceTokens = source.getTokens();
        
        //Get pos tags or target sentence:
        ArrayList<String> sourcePOSTags = (ArrayList<String>) source.getValue("postags");
        
        //Get alignments object:
        HashMap<Integer, Integer> alignments = (HashMap<Integer, Integer>) target.getValue("alignments.file");
        
        //Get POS tags of each target word:
        for(int i=0; i<targetTokens.length; i++){
            //Get index of aligned word in source sentence:
            Integer alignedIndex = alignments.get(i);
            
            //Get aligned word POS tag:
            String alignedPOSTag;
            if(alignedIndex!=null){
                alignedPOSTag = sourcePOSTags.get(alignedIndex);
            }else{
                alignedPOSTag = "NULL";
            }
            
            String value = this.getIdentifier()+'='+alignedPOSTag;
            result[i] = value;
        }
        
        //Save values produced:
        this.setValues(result);
    }
}
