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
public class Feature9996 extends Feature{
    public Feature9996() {
		// TODO Auto-generated constructor stub
		setIndex(9996);
		
	}

    @Override
    public void run(Sentence source, Sentence target) {
        Float targetValue = (Float) target.getValue("noun_repetition_target");
        Float sourceValue = (Float) source.getValue("noun_repetition_source");
        Float ratio = 0f;
        if (sourceValue!=0){
            ratio = targetValue/sourceValue;
        }
        setValue(ratio);
    }
    
}
