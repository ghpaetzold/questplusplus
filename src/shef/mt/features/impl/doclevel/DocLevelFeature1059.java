/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.ArrayList;
import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.LanguageModel;

/**
 * Percentage of distinct bigrams seen in the corpus (in all quartiles) - document-level
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1059 extends DocLevelFeature {

    private int size = 2;

    public DocLevelFeature1059() {
        this.setIndex(1059);
        this.setDescription(" percentage of distinct bigrams seen in the corpus (in all quartiles) - document-level");
        this.addResource("source.ngram");

    }
    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub

    }

    @Override
    public void run(Doc source, Doc target) {
        float total=0.0f;
        
        for(int i=0;i<source.getSentences().size();i++){
            ArrayList<String> ngrams = source.getSentence(i).getNGrams(size);
            HashSet<String> unique = new HashSet<String>(ngrams);
            if (unique.size() == 0) {
                setValue(0);
                return;
            }
            int count = 0;
            LanguageModel lm = (LanguageModel)source.getSentence(i).getValue("ngramcount");
            for (String ngram : unique) {
                if (lm.getFreq(ngram, size) > 0) {
                    count++;
                }
            }
            total+=count / (float) unique.size();
        }
        setValue((float) total/source.getSentences().size());
    }
}
