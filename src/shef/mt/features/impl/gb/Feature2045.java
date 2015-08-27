/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.Translation;
import shef.mt.features.impl.Feature;
import java.util.*;

/**
 * averaged target word statistics: score-weighted relative frequency of the
 * word in the n-best list
 *
 * @author Catalina Hallett
 *
 */
public class Feature2045 extends Feature {

    public Feature2045() {
        setIndex("2045");
        setDescription("averaged target word statistics: score-weighted relative frequency of the word in the n-best list ");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        Translation best = source.getBest();
//		float score = Float.parseFloat(best.getAttribute("log_prob_feat"));
        float score = Float.parseFloat(best.getAttribute("prob"));
        float relFreq = (Float) source.getValue("nbestRelWordFreq");
        setValue(score * relFreq);

    }
}
