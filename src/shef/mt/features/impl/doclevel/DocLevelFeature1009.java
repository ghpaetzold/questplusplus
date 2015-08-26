/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import java.util.ArrayList;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.Doc;
import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;

/**
 * Source document log probability 
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1009 extends DocLevelFeature {

    public DocLevelFeature1009() {
        setIndex(1009);
        setDescription("source document log probability");
        this.addResource("source.lm");
    }


    @Override
    public void run(Doc source, Doc target) {
        ArrayList<Sentence> sentences = source.getSentences();
        double doc_log_prob = 0.0;
        for(int i=0; i<sentences.size();i++){
            
            doc_log_prob+=(float) sentences.get(i).getValue("logprob");
            
        }
        
        setValue((float) doc_log_prob/sentences.size());
    }

    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
