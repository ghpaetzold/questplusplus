/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import java.util.HashSet;

import shef.mt.features.impl.Feature;

/**
 * MOSES: number of recombined graph nodes
 *
 * @author cat
 *
 */
public class Feature2060 extends Feature {

    public Feature2060() {
        setIndex("2060");
        setDescription("number of recombined graph nodes");
        this.addResource("moses.xml");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((String) source.getValue("recombined")));
    }
}
