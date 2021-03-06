/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.FileModel;
import shef.mt.tools.Giza;

/**
 * average number of translations per source word in the sentence (threshold in
 * giza: prob > 0.1) weighted by the inverse frequency of each word in the
 * source corpus
 *
 * @author Catalina Hallett
 *
 */
public class Feature1040 extends Feature {

    final static Float probThresh = 0.1f;

    public Feature1040() {
        setIndex(1040);
        setDescription("average number of translations per source word in the sentence (threshold in giza: prob > 0.1) weighted by the inverse frequency of each word in the source corpus");
        addResource("giza.path");
        addResource("source.corpus");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        float noTokens = source.getNoTokens();

        float probSum = 0;

        String[] tokens = source.getTokens();
        for (String word : tokens) {
            float freq = FileModel.getFrequency(word);
            float gizaf = (float) Giza.getWordProbabilityCount(word, probThresh);

            if (freq != 0) {
                probSum += gizaf / freq;
            }
        }

        if (noTokens == 0) {
            setValue(0);
        } else {
            setValue(probSum / noTokens);
        }
    }
}
