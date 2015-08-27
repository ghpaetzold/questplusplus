/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import java.util.HashSet;

import shef.mt.features.impl.Feature;

/**
 * MOSES: pruned search graph nodes
 *
 * @author cat
 *
 */
public class Feature2058 extends Feature {

    public Feature2058() {
        setIndex("2058");
        setDescription("MOSES: pruned search graph nodes");
        this.addResource("moses.xml");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((String) source.getValue("pruned")));

    }
}
