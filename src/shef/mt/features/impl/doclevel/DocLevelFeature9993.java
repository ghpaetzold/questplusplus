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
 * Ratio lemma repetition between target and source documents
 * 
 * @author Carolina Scaton
 */
public class DocLevelFeature9993 extends DocLevelFeature{
    public DocLevelFeature9993() {
		// TODO Auto-generated constructor stub
		this.setIndex(9993);
                this.setDescription("Ratio lemma repetition between target and source documents");
		this.addResource("source.postagger");
                this.addResource("target.postagger");
                this.addResource("discrep");
	}

    @Override
    public void run(Doc source, Doc target) {
        Float targetValue = (Float) target.getValue("lemma_repetition");
        Float sourceValue = (Float) source.getValue("lemma_repetition");
        Float ratio = 0f;
        if (sourceValue!=0){
            ratio = targetValue/sourceValue;
        }
        
        setValue(ratio);
    }

    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
