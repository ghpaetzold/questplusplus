/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between the number of PP phrases in the source and target
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1104 extends Feature {

    public Feature1104() {
        setIndex("1104");
        HashSet res = new HashSet();
        setDescription("absolute difference between the number of PP phrases in the source and target");

        res.add("stf");
        setResources(res);
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        float sourcePP = (Integer) source.getValue("PP");
        float targetPP = (Integer) target.getValue("PP");
        setValue(Math.abs(sourcePP - targetPP));

    }
}
