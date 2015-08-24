package shef.mt.features.impl.bb;

import shef.mt.tools.Giza2;
import java.util.HashSet;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * average number of translations per source word in the sentence (threshold in
 * giza2: prob > 0.01)
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1017 extends Feature {

    final static Float probThresh = 0.01f;

    public Feature1017() {
        setIndex(1017);
        setDescription("average number of translations per source word in the sentence (threshold in giza2: prob > 0.01)");
        this.addResource("Giza2");
    }

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
