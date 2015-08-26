/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import java.util.HashMap;

import shef.mt.features.impl.DocLevelFeature;

import shef.mt.features.util.Doc;
import shef.mt.features.util.FeatureManager;
import shef.mt.features.util.Sentence;
import shef.mt.tools.PosTagger;
import shef.mt.util.PropertiesManager;

/**
 *
 * Word repetition in target document
 * 
 * @author Carolina Scarton
 */
public class DocLevelFeature9988 extends DocLevelFeature {
    
    public DocLevelFeature9988() {
		// TODO Auto-generated constructor stub
		this.setIndex(9988);
                this.setDescription("Word repetition target");
                this.addResource("target.postagger");
                this.addResource("discrep");
		
	}

    @Override
    public void run(Doc source, Doc target) {

        setValue((Float) target.getValue("word_repetition"));
    }
    @Override
    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
