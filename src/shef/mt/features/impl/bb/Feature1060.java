/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.ArrayList;
import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.LanguageModel;

/**
 * percentage of distinct trigrams seen in the corpus (in all quartiles)
 *
 * @author Catalina Hallett
 *
 */
public class Feature1060 extends Feature {

    private int size = 3;

    public Feature1060() {
        setIndex(1060);
        setDescription("percentage of distinct trigrams seen in the corpus (in all quartiles)");
        this.addResource("source.ngram");
    }
    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        ArrayList<String> ngrams = source.getNGrams(size);
        HashSet<String> unique = new HashSet<String>(ngrams);
        int count = 0;
        LanguageModel lm = (LanguageModel)source.getValue("ngramcount");
        for (String ngram : unique) {
            if (lm.getFreq(ngram, size) > 0) {
                count++;
            }
        }
        if (unique.size() == 0) {
            setValue(0);
        } else {
            setValue(count / (float) unique.size());
        }
    }
}
