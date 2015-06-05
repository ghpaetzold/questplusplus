/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between the number of ADVPs in the source and target
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1112 extends Feature {

    public Feature1112() {
        setIndex(1112);
        setDescription("absolute difference between the number of ADVPs in the source and target");
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
        float sourceP = (Integer) source.getValue("ADVP");
        float targetP = (Integer) target.getValue("ADVP");
        setValue(Math.abs(sourceP - targetP));



    }
}
