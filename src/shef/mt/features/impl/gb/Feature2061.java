/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import java.util.HashSet;

import shef.mt.features.impl.Feature;

/**
 * MOSES: proportion of recombined graph nodes
 *
 * @author cat
 *
 */
public class Feature2061 extends Feature {

    public Feature2061() {
        setIndex("2061");
        setDescription("proportion of recombined graph nodes");
        this.addResource("moses.xml");
    }

    public void run(Sentence source, Sentence target) {
        float total = new Float((String) source.getValue("totalHypotheses"));
        float disc = new Float((String) source.getValue("recombined"));
        setValue(disc / total);

    }
}
