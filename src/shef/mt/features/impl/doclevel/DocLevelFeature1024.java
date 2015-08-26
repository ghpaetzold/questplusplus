/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;
import java.util.StringTokenizer;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.Doc;
import shef.mt.tools.Giza;

/**
 * Average number of translations per source word in the document (threshold in
 * giza1: prob > 0.5)
 *
 * @author Carolina Scarton
 *
 *
 */
public class DocLevelFeature1024 extends DocLevelFeature {

    final static Float probThresh = 0.50f;

    public DocLevelFeature1024() {
        this.setIndex(1024);
        this.setDescription("average number of translations per source word in the document (threshold in giza1: prob > 0.5)");
        this.addResource("giza.path");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
    }

    @Override
    public void run(Doc source, Doc target) {
        // TODO Auto-generated method stub
        float noTokens = 0;
        float probSum = 0;
        for (int i=0;i<source.getSentences().size();i++){
            String[] tokens = source.getSentence(i).getTokens();
            
            float value;
            for (String word : tokens) {
                value = Giza.getWordProbabilityCount(word.toLowerCase(), probThresh);
                probSum += value;
            }
            noTokens+=source.getSentence(i).getNoTokens();
        }
        if (noTokens==0){
            setValue(0);
       
        }else{
           setValue((float) probSum / noTokens);
        }

    }
}
