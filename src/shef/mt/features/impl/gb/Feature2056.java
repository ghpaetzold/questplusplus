/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import java.util.HashSet;

import shef.mt.features.impl.Feature;

/**
 * MOSES: discarded search graph nodes
 *
 * @author cat
 *
 */
public class Feature2056 extends Feature {

    public Feature2056() {
        setIndex("2056");
        setDescription("discarded search graph nodes");
        this.addResource("moses.xml");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((String) source.getValue("discarded")));
    }
}
