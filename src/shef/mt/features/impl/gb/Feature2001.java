/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Translation;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import java.util.*;

/**
 * log prob score of the hypothesis
 *
 * @author cat
 *
 */
public class Feature2001 extends Feature {

    public Feature2001() {
        setIndex("2001");
        setDescription("log prob score of the hypothesis");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        Translation best = source.getBest();
//		float value = Float.parseFloat(best.getAttribute("log_prob_feat"));
        float value = Float.parseFloat(best.getAttribute("prob"));
        setValue(value);
    }
}
