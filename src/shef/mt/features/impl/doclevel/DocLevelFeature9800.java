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
 * Number of sentences in the source document
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature9800 extends DocLevelFeature {
    
    public DocLevelFeature9800() {
		// TODO Auto-generated constructor stub
		this.setIndex(9800);
                this.setDescription("Number of sentences in the source document");
		
	}

    @Override
    public void run(Doc source, Doc target) {
        setValue(source.getSentences().size());
    }

    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
