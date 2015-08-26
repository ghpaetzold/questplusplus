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
 * Noun repetition in target document
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature9994 extends DocLevelFeature{
    public DocLevelFeature9994() {
		// TODO Auto-generated constructor stub
		this.setIndex(9994);
                this.setDescription("Noun repetition in target document");
                this.addResource("target.postagger");
                this.addResource("discrep");
		
	}
    @Override
    public void run(Doc source, Doc target) {
        setValue((Float) target.getValue("noun_repetition"));
    }

    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
