/**
 *
 */
package shef.mt.features.impl.gb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.Translation;

/**
 * log probability score (base model) normalized by source sentence length
 *
 * @author Catalina Hallett
 *
 */
public class Feature2002 extends Feature {

    public Feature2002() {
        setIndex("2002");
        setDescription("log probability score (base model) normalized by source sentence length");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        Translation best = source.getBest();
        float value = Float.parseFloat(best.getAttribute("prob"));
//		float value = Float.parseFloat(best.getAttribute("prob"));
        setValue(value / source.getNoTokens());
    }
}
