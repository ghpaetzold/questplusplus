/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import java.util.ArrayList;
import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 *
 * Target document perplexity
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature1013 extends DocLevelFeature {
    
    public DocLevelFeature1013() {
        setIndex(1013);
        setDescription("perplexity of the target document");
        this.addResource("target.lm");
        
    }

    @Override
    public void run(Doc source, Doc target) {
        ArrayList<Sentence> sentences = target.getSentences();
        double doc_log_prob = 0.0;
        for(int i=0; i<sentences.size();i++){
            doc_log_prob+=(float) sentences.get(i).getValue("ppl");
        }
        setValue((float) doc_log_prob/sentences.size());
    }

    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
