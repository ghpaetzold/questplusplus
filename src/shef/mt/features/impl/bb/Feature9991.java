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
public class Feature9991 extends Feature {

    public Feature9991() {
        // TODO Auto-generated constructor stub
        setIndex(9991);
    }

    @Override
    public void run(Sentence source, Sentence target) {
        setValue((Float) source.getValue("lemma_repetition_source"));
    }

}
