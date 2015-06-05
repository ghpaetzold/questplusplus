/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between the number of VPs in the source and target
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1108 extends Feature {

    public Feature1108() {
        setIndex("1108");
        setDescription("absolute difference between the number of VPs in the source and target");
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
        float sourceP = (Integer) source.getValue("VP");
        float targetP = (Integer) target.getValue("VP");
        setValue(Math.abs(sourceP - targetP));



    }
}
