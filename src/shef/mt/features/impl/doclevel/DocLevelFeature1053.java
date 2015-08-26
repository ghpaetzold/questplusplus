/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.LanguageModel;

/**
 * Average bigram frequency in quartile 4 of frequency (lower frequency words)
 * in the corpus of the source document
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1053 extends DocLevelFeature {

    static int size = 2;
    static int quart = 4;

    public DocLevelFeature1053() {
        this.setIndex(1053);
        this.setDescription("average bigram frequency in quartile 4 of frequency (lower frequency words) in the corpus of the source document");
        this.addResource("source.ngram");
    }


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
            int cutOffLow = lm.getCutOff(size, quart - 1);
            int cutOffHigh = lm.getCutOff(size, quart);
            while (it.hasNext()) {
                ngram = it.next();
                freq = lm.getFreq(ngram, size);
                if (freq <= cutOffHigh && freq > cutOffLow) {
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