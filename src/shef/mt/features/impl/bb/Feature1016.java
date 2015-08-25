package shef.mt.features.impl.bb;

import java.util.HashSet;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.Giza;

/**
 * average number of translations per source word in the sentence (threshold in
 * giza1: prob > 0.01)
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1016 extends Feature {

    final static Float probThresh = 0.01f;

    public Feature1016() {
        setIndex(1016);
        setDescription("average number of translations per source word in the sentence (threshold in giza1: prob > 0.01)");
        this.addResource("giza.path");
    }

    @Override
    public void run(Sentence source, Sentence target) {

        float noTokens = source.getNoTokens();

        String[] tokens = source.getTokens();
        float probSum = 0;
        float value;
        for (String word : tokens) {
            value = Giza.getWordProbabilityCount(word.toLowerCase(), probThresh);
            probSum += value;
        }

        if (noTokens == 0) {
            setValue(0);
        } else {
            setValue(probSum / noTokens);
        }
    }
}
