/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import java.util.HashSet;

import shef.mt.features.impl.Feature;

/**
 * MOSES: proportion of pruned search graph nodes
 *
 * @author cat
 *
 */
public class Feature2059 extends Feature {

    public Feature2059() {
        setIndex("2059");
        setDescription("proportion of pruned search graph nodes ");
        this.addResource("moses.xml");
    }

    public void run(Sentence source, Sentence target) {
        float total = new Float((String) source.getValue("totalHypotheses"));
        float pruned = new Float((String) source.getValue("pruned"));
        setValue(pruned / total);

    }
}
