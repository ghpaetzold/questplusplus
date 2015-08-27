/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 *
 * ratio of percentage of pronouns in the source and target
 *
 * @author cat
 *
 */
public class Feature1094 extends Feature {

    public Feature1094() {
        setIndex(1094);
        setDescription("ratio of percentage of pronouns in the source and target");
        this.addResource("source.postagger");
        this.addResource("target.postagger");
    }
    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float noWords = source.getNoTokens();
        float noContent = (Integer) source.getValue("prons");
        float perc1 = (float) noContent / noWords;
        noWords = target.getNoTokens();
        noContent = (Integer) target.getValue("prons");
        float perc2 = (float) noContent / noWords;
        if (perc2 == 0) {
            setValue(0);
        } else {
            setValue(perc1 / perc2);
        }


    }
}
