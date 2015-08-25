/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.tools.LanguageModel;
import java.util.HashSet;
import java.util.*;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * average unigram frequency in quartile_1 of frequency (lower frequency words)
 * in the corpus of the source sentence
 *
 * @author Catalina Hallett
 *
 */
public class Feature1046 extends Feature {

    static int size = 1;
    static int quart = 1;

    public Feature1046() {
        setIndex(1046);
        setDescription("average unigram frequency in quartile_1 of frequency (lower frequency words) in the corpus of the source sentence");
        this.addResource("source.ngram");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        ArrayList<String> ngrams = source.getNGrams(size);
        Iterator<String> it = ngrams.iterator();
        String ngram;
        int count = 0;
        int freq;
        int cutOff;
        LanguageModel lm = (LanguageModel) source.getValue("ngramcount");
        cutOff = lm.getCutOff(size, quart);
        while (it.hasNext()) {
            ngram = it.next();
            freq = lm.getFreq(ngram, size);
            if (freq <= cutOff && freq > 0) {
                count++;
            }
        }
        setValue((float) count / ngrams.size());
    }
}
