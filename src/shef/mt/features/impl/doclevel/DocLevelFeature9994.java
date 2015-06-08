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
 * @author carol
 */
public class DocLevelFeature9994 extends DocLevelFeature{
    public DocLevelFeature9994() {
		// TODO Auto-generated constructor stub
		setIndex(9994);
                this.addResource("postagger");
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
