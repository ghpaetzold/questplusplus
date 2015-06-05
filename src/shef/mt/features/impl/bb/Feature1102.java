/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * number of prepositional phrases in the source
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1102 extends Feature {

    public Feature1102() {
        setIndex("1102");
        HashSet res = new HashSet();
        setDescription("number of prepositional phrases in the source");
        //requires named entities
        res.add("stf");
        setResources(res);
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        setValue((Integer) source.getValue("PP"));


    }
}
