/**
 *
 */
package shef.mt.features.impl.bb;


import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.FileModel;
import shef.mt.tools.Giza;

/**
 * average number of translations per source word in the sentence (threshold in
 * giza: prob > 0.01) weighted by the inverse frequency of each word in the
 * source corpus
 *
 * @author Catalina Hallett
 *
 */
public class Feature1036 extends Feature {

    final static Float probThresh = 0.01f;

    public Feature1036() {
        setIndex(1036);
        setDescription("average number of translations per source word in the sentence (threshold in giza: prob > 0.01) weighted by the inverse frequency of each word in the source corpus");
        this.addResource("giza.path");
        this.addResource("source.corpus");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
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
