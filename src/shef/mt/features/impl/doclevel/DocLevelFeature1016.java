/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.Giza;

/**
 *
 * Average number of translations per source word in the sentence (threshold in giza1: prob > 0.01)
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature1016 extends DocLevelFeature {

    final static Float probThresh = 0.01f;
    
    public DocLevelFeature1016() {
        this.setIndex(1016);
        this.setDescription("average number of translations per source word in the sentence (threshold in giza1: prob > 0.01)");
        this.addResource("giza.path");
    }
    
    @Override
    public void run(Doc source, Doc target) {
        float noTokens = 0;
        float probSum = 0;
        for (int i=0;i<source.getSentences().size();i++){
            String[] tokens = source.getSentence(i).getTokens();
            float value;
            for (String word : tokens) {
                value = Giza.getWordProbabilityCount(word.toLowerCase(), probThresh);
                probSum += value;
            }
            noTokens+=source.getSentence(i).getNoTokens();
        }
        if (noTokens == 0) {
            setValue(0);
        } else {
            setValue(probSum / noTokens);
        }
        
    }

    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
