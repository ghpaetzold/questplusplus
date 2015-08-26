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
 * Ratio word repetition in target and source documents
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature9992 extends DocLevelFeature{
    
    public DocLevelFeature9992() {
		// TODO Auto-generated constructor stub
		this.setIndex(9992);
                this.setDescription("Ratio word repetition in target and source documents");
                this.addResource("target.postagger");
                this.addResource("source.postagger");
                this.addResource("discrep");
		
	}

    @Override
    public void run(Doc source, Doc target) {
        Float targetValue = (Float) target.getValue("word_repetition");
        Float sourceValue = (Float) source.getValue("word_repetition");
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
