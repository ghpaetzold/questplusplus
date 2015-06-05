/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import java.util.HashSet;

/**
 *
 *
 * number of occurrences of the target word within the target hypothesis
 * (averaged for all words in the hypothesis - type/token ratio)
 */
public class Feature1015 extends Feature {

    public Feature1015() {
        setIndex(1015);
        setDescription("number of occurrences of the target word within the target hypothesis (averaged for all words in the hypothesis - type/token ratio)");

    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub

        int noTokens = target.getNoTokens();
        String[] tokens = target.getTokens();

        HashSet<String> uniqueTokens = new HashSet<String>();
        for (String token : tokens) {
            uniqueTokens.add(token.toLowerCase());
        }
        if (uniqueTokens.isEmpty()) {
            setValue(0);
        } else {
            setValue((float) noTokens / uniqueTokens.size());
        }
        return;

    }
}
