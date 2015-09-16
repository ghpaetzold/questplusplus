/**
 *
 */
package shef.mt.features.impl.bb;


import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.Giza;

/**
 * average number of translations per source word in the sentence (threshold in
 * giza1: prob > 0.2)
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1022 extends Feature {

    final static Float probThresh = 0.20f;

    public Feature1022() {
        setIndex(1022);
        setDescription("average number of translations per source word in the sentence (threshold in giza1: prob > 0.2)");
        this.addResource("giza.path");
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
            value = Giza.getWordProbabilityCount(word.toLowerCase(), probThresh);
            probSum += value;
        }

        float result = probSum / noTokens;

        setValue(result);
    }
}
