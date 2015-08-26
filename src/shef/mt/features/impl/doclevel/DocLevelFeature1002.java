/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;
import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import java.util.*;
import shef.mt.features.impl.bb.Feature1002;


    
/**
 * Number of tokens in the target document
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature1002 extends DocLevelFeature {
    public DocLevelFeature1002(){
        this.setIndex(1002);
        this.setDescription("number of tokens in the target document");
    }

    @Override
    public void run(Doc source, Doc target) {
        ArrayList<Sentence> sentences = target.getSentences();
        int n_tokens = 0;
        for (int i=0;i<sentences.size();i++){
            n_tokens+=sentences.get(i).getNoTokens();
        }
        setValue(n_tokens);

    }

    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

