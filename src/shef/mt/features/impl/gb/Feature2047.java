/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.Translation;
import shef.mt.features.impl.Feature;

import java.util.*;

/**
 * relative frequency of the word in the n-best list occurring in the same
 * position as the target word multiplied by the log prob
 *
 * @author cat
 *
 */
public class Feature2047 extends Feature {

    public Feature2047() {
        setIndex("2047");
        setDescription("relative frequency of the word in the n-best list occurring in the same position as the target word multiplied by the log prob");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float value = (Float) source.getValue("wordPos");
        Translation best = source.getBest();
//		float score = Float.parseFloat(best.getAttribute("log_prob_feat"));
        float score = Float.parseFloat(best.getAttribute("prob"));
        setValue(value * score);
    }
}
