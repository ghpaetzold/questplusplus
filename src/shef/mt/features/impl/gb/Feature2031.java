/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Translation;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import java.util.*;

/**
 * IBM m1t2s
 *
 * @author cat
 *
 */
public class Feature2031 extends Feature {

    public Feature2031() {
        setIndex("2031");
        setDescription("IBM m1t2s");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        Translation best = source.getBest();
        float value = Float.parseFloat(best.getAttribute("m1t2s"));
        setValue(value);
    }
}
