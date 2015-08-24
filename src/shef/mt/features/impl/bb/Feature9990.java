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
public class Feature9990 extends Feature {

    public Feature9990() {
        // TODO Auto-generated constructor stub
        setIndex(9990);
    }

    @Override
    public void run(Sentence source, Sentence target) {
        setValue((Float) source.getValue("word_repetition_source"));
    }

}
