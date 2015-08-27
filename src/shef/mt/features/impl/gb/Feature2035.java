/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Translation;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import java.util.*;

/**
 * IBM deletion_score
 *
 * @author cat
 *
 */
public class Feature2035 extends Feature {

    public Feature2035() {
        setIndex("2035");
        setDescription("IBM deletion score");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        Translation best = source.getBest();
        float value = Float.parseFloat(best.getAttribute("deletion_score"));
        setValue(value);
    }
}
