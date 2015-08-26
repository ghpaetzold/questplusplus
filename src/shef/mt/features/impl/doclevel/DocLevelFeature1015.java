/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;
import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Number of occurrences of the target word within the target hypothesis (averaged for all words in the hypothesis - type/token ratio)
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature1015 extends DocLevelFeature{
    
    public DocLevelFeature1015() {
        this.setIndex(1015);
        this.setDescription("number of occurrences of the target word within the target hypothesis (averaged for all words in the hypothesis - type/token ratio)");
        
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet.");
        

    }

    @Override
    public void run(Doc source, Doc target) {
        HashSet<String> uniqueTokens = new HashSet<String>();
        int noTokens=0;
        for(int i=0; i<target.getSentences().size(); i++){
            noTokens+= target.getSentence(i).getNoTokens();
            String[] tokens = target.getSentence(i).getTokens();
            for (String token : tokens) {
                uniqueTokens.add(token.toLowerCase());
            }
            
        }
        if (uniqueTokens.isEmpty()) {
            setValue(0);
        } else {
            setValue((float) noTokens / uniqueTokens.size()); 
        }
        
    }


}
