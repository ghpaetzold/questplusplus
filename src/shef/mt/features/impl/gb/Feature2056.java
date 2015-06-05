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
        HashSet<String> res = new HashSet<String>();
        res.add("discarded");
        setResources(res);
        setDescription("discarded search graph nodes");

    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((String) source.getValue("discarded")));
    }
}
