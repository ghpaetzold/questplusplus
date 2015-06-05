/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between the number of ADJPs in the source and target
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1110 extends Feature {

    public Feature1110() {
        setIndex(1110);
        setDescription("absolute difference between the number of ADJPs in the source and target");
        HashSet res = new HashSet();
        //requires named entities
        res.add("stf");
        setResources(res);
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        float sourceP = (Integer) source.getValue("ADJP");
        float targetP = (Integer) target.getValue("ADJP");
        setValue(Math.abs(sourceP - targetP));



    }
}
