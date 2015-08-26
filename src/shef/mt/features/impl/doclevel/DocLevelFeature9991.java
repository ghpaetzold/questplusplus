/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import java.util.HashMap;
import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.PosTagger;

/**
 *
 * Lemma repetition in source document
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature9991 extends DocLevelFeature{

    public DocLevelFeature9991() {
		// TODO Auto-generated constructor stub
		this.setIndex(9991);
                this.setDescription("Lemma repetition source document");
                this.addResource("source.postagger");
                this.addResource("discrep");
		
	}

    @Override
    public void run(Doc source, Doc target) {
        setValue((Float) source.getValue("lemma_repetition"));
    }
    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
