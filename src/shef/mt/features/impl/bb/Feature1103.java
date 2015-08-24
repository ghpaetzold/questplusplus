/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * number of prepositional phrases in the target
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1103 extends Feature {

    public Feature1103() {
        setIndex("1103");
        setDescription("number of prepositional phrases in the target");
        this.addResource("stf");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        setValue((Integer) target.getValue("PP"));


    }
}
