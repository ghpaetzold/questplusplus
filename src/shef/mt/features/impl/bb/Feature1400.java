package shef.mt.features.impl.bb;

import java.util.HashSet;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.GlobalLexicon;

/**
 * geometric average of target word probabilities under Global Lexicon Model
 * 
 * The Global Lexicon Model is based on a large number of log-linear binary
 * classifiers as suggested by Mauser et al. (2009). There is one classifier for
 * each possible target word, giving the probability of that word appearing in 
 * the translation of the given sentence:
 *
 *  p(e|f_1^J)
 *
 * where e is the target word in question and f_1^J = f_1, .., f_J denotes the
 * source sentence.
 *
 * @author Christian Buck
 */

public class Feature1400 extends Feature {

    final static Float probThresh = 0.01f;

    public Feature1400() {
        setIndex(1400);
        setDescription("geometric average of target word probabilities under Global Lexicon Model");
        this.addResource("globallexicon");
    }

    public static Double geometricMean(Double[] probs) {
        Double sum = 0.0;
        for (Double w: probs) {
            sum += w;
        }
        return Math.pow(sum, 1.0 / probs.length);
    }
        
    @Override
    public void run(Sentence source, Sentence target) {
        String[] s_tokens = source.getTokens();
        String[] t_tokens = target.getTokens();
        Double[] probs = new Double[t_tokens.length];
        
        Integer prob_idx = 0;
        for (String tw : t_tokens) {
            Double p = GlobalLexicon.getBias(tw);
            for (String sw : s_tokens) {
                p += GlobalLexicon.get(sw, tw);
            }
            p = 1./(1.+ Math.exp(-p));
            probs[prob_idx] = p;
            prob_idx++;
        }
        setValue(geometricMean(probs).floatValue());
    }
}
