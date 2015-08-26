/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import java.util.ArrayList;
import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.Doc;
import shef.mt.tools.LanguageModel;

/**
 *
 * Percentage of distinct unigrams seen in the corpus (in all quartiles) - document-level
 *
 * @author Carolina scarton
 *
 */
public class DocLevelFeature1058 extends DocLevelFeature {

    private int size = 1;

    public DocLevelFeature1058() {
        this.setIndex(1058);
        this.setDescription("percentage of distinct unigrams seen in the corpus (in all quartiles) - document-level");
        this.addResource("source.ngram");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run(Doc source, Doc target) {
        float total = 0.0f;
        for(int i=0;i<source.getSentences().size();i++){
            ArrayList<String> ngrams = source.getSentence(i).getNGrams(size);
            HashSet<String> unique = new HashSet<String>(ngrams);
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
