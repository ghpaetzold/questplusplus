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
 * Ratio noun repetition between target and source documents
 * 
 * @author carol
 */
public class DocLevelFeature9996 extends DocLevelFeature{
    public DocLevelFeature9996() {
		// TODO Auto-generated constructor stub
		this.setIndex(9996);
                this.setDescription("Ratio noun repetition between target and source docuemnts");
		this.addResource("target.postagger");
                this.addResource("source.postagger");
                this.addResource("discrep");
	}

    @Override
    public void run(Doc source, Doc target) {
        Float targetValue = (Float) target.getValue("noun_repetition");
        Float sourceValue = (Float) source.getValue("noun_repetition");
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
