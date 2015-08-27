/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Translation;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import java.util.*;

/**
 * MOSES phrase probability phrase_prob_feat_5
 *
 * @author cat
 *
 */
public class Feature2025 extends Feature {

    public Feature2025() {
        setIndex("2025");
        setDescription("MOSES phrase probability phrase_prob_feat_5");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        Translation best = source.getBest();
        float value = Float.parseFloat(best.getAttribute("phrase_prob_feat_5"));
        setValue(value);
    }
}
