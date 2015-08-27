/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import java.util.HashSet;

import shef.mt.features.impl.Feature;

/**
 * MOSES: totalHypotheses
 *
 * @author cat
 *
 */
public class Feature2055 extends Feature {

    public Feature2055() {
        setIndex("2055");
        setDescription("MOSES: totalHypotheses");
        this.addResource("moses.xml");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((String) source.getValue("totalHypotheses")));
    }
}
