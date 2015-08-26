/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.FileModel;
import shef.mt.tools.Giza;

/**
 *
 * Average number of translations per source word in the document (threshold in
 * giza: prob > 0.2) weighted by the inverse frequency of each word in the
 * source corpus
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1042 extends DocLevelFeature {

    final static Float probThresh = 0.2f;

    public DocLevelFeature1042() {
        setIndex(1042);
        setDescription("average number of translations per source word in the document (threshold in giza: prob > 0.2) weighted by the inverse frequency of each word in the source corpus");
        this.addResource("giza.path");
        //res.add("Freq");
        
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {

    }

    @Override
    public void run(Doc source, Doc target) {
        float noTokens = 0;

        float probSum = 0;
        for (int i=0;i<source.getSentences().size();i++){
            String[] tokens = source.getSentence(i).getTokens();
            for (String word : tokens) {
                float freq = FileModel.getFrequency(word);
                float gizaf = (float) Giza.getWordProbabilityCount(word, probThresh);

                if (freq != 0) {
                    probSum += gizaf / freq;
                }
            }
            noTokens+=source.getSentence(i).getNoTokens();
        }

        if (noTokens == 0) {
            setValue(0);
        } else {
            setValue(probSum / noTokens);
        }
    }
}
