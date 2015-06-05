/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 *
 * @author carol
 */
public class Feature9992 extends Feature{
    
    public Feature9992() {
		// TODO Auto-generated constructor stub
		setIndex(9992);
		
	}

    @Override
    public void run(Sentence source, Sentence target) {
        Float targetValue = (Float) target.getValue("word_repetition_target");
        Float sourceValue = (Float) source.getValue("word_repetition_source");
        Float ratio = 0f;
        if (sourceValue!=0){
            ratio = targetValue/sourceValue;
        }
        setValue(ratio);
    }
    
}
