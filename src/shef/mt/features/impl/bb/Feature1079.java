/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;

/**
 * absolute difference between number of numbers in the source and target
 * sentence normalised by source sentence length
 *
 * @author cat
 *
 */
public class Feature1079 extends Feature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public Feature1079() {
        setIndex(1079);
        setDescription("absolute difference between number of numbers in the source and target sentence normalised by source sentence length");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float noTokensTarget = target.getNoTokens();

        float noNrSource = (Integer) source.getValue("noNumbers");
        float noNrTarget = (Integer) target.getValue("noNumbers");
        setValue(Math.abs(noNrSource - noNrTarget) / noTokensTarget);
    }
}
