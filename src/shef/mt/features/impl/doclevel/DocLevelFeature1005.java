/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 *
 * Absolute difference between no tokens and source and target normalised by source length
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature1005 extends DocLevelFeature {
    public DocLevelFeature1005() {
        this.setIndex(1005);
        this.setDescription("absolute difference between no tokens and source and target normalised by source length");
    }

    @Override
    public void run(Doc source, Doc target) {
        int sourceTok = 0;
        int targetTok = 0;
        for(int i=0;i<source.getSentences().size();i++){
            sourceTok+=source.getSentence(i).getNoTokens();
            targetTok+=target.getSentence(i).getNoTokens();
        }
        if (sourceTok == 0) {
            setValue(0);
        } else {
            setValue((float) Math.abs(targetTok - sourceTok) / sourceTok);
        }
    }

    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
