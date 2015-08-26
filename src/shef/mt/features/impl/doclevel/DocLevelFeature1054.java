/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.Doc;
import shef.mt.tools.LanguageModel;

/**
 * Average trigram frequency in quartile 1 of frequency (lower frequency words)
 * in the corpus of the source document
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1054 extends DocLevelFeature {

    static int size = 3;
    static int quart = 1;

    public DocLevelFeature1054() {
        this.setIndex(1054);
        this.setDescription("average trigram frequency in quartile 1 of frequency (lower frequency words) in the corpus of the source document");
        this.addResource("source.ngram");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run(Doc source, Doc target) {
        float total=0.0f;
        for(int i=0;i<source.getSentences().size();i++){
            ArrayList<String> ngrams = source.getSentence(i).getNGrams(size);
            Iterator<String> it = ngrams.iterator();
            String ngram;
            int count = 0;
            int freq;
            int totalFreq = 0;
            LanguageModel lm = (LanguageModel) source.getSentence(i).getValue("ngramcount");
            int cutOff = lm.getCutOff(size, quart);
            while (it.hasNext()) {
                ngram = it.next();
                freq = lm.getFreq(ngram, size);
                if (freq <= cutOff && freq > 0) {
                    count++;
                }
            }
            if (count == 0 || ngrams.size()==0) {
                total+=0;
            } else {
                total+=(float) count / ngrams.size();
            }
        }
        setValue((float) total/source.getSentences().size());
    }
}
