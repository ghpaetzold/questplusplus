/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.impl.bb.Feature1001;
import shef.mt.features.impl.bb.Feature1002;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 *
 * Ratio of number of tokens in the target and source documents
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature1004 extends DocLevelFeature {
    
    public DocLevelFeature1004() {
        this.setIndex(1004);
        this.setDescription("no tokens in the target / no tokens in the source");
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
            setValue((float) targetTok / sourceTok);
        }
    }

    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
