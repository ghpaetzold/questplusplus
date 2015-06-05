/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.tools.Giza2;
import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;

import java.util.*;

/**
 * average number of translations per source word in the sentence (threshold in
 * giza2: prob > 0.05)
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1019 extends Feature {

    final static Float probThresh = 0.05f;

    public Feature1019() {
        setIndex(1019);
        setDescription("average number of translations per source word in the sentence (threshold in giza2: prob > 0.05)");
        HashSet res = new HashSet<String>();
        res.add("Giza2");
        setResources(res);
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float noTokens = source.getNoTokens();
        String[] tokens = source.getTokens();
        float probSum = 0;
        float value;
        for (String word : tokens) {
            value = Giza2.getWordProbabilityCount(word.toLowerCase(), probThresh);
            probSum += value;
        }

        float result = probSum / noTokens;

        setValue(result);
    }
}
