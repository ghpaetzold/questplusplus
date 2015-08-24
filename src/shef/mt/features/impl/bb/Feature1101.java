/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between the depth of the syntactic tree for the source
 * and the depth of the syntactic tree for the target
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1101 extends Feature {

    public Feature1101() {
        setIndex(1101);
        setDescription("absolute difference between the depth of the syntactic tree for the source and the depth of the syntactic tree for the target");
        this.addResource("stf");
    }

    public void run(Sentence source, Sentence target) {
        int dptSource = (Integer) source.getValue("depth");
        int dptTarget = (Integer) target.getValue("depth");
        setValue(Math.abs(dptSource - dptTarget));
    }
}
