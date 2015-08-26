/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;
import java.util.StringTokenizer;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.FileModel;
import shef.mt.tools.Giza;

/**
 * Average word frequency: on average, each type (unigram) in the source
 * document appears x times in the corpus (in all quartiles)
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1061 extends DocLevelFeature {

    public DocLevelFeature1061() {
        this.setIndex(1061);
        this.setDescription("average word frequency: on average, each type (unigram) in the source document appears x times in the corpus (in all quartiles)");
        this.addResource("source.ngram");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub


    }

    @Override
    public void run(Doc source, Doc target) {
        float noTokens=0;
        float wordSum = 0;
        for(int i=0;i<source.getSentences().size();i++){

            String[] tokens = source.getSentence(i).getTokens();

            
            for (String word : tokens) {
                wordSum += FileModel.getFrequency(word);
            }
            noTokens+=source.getSentence(i).getNoTokens();
        }

        float result = wordSum / noTokens;
        if (result == 0) {
            setValue(0);
        } else {
            setValue(result);
        }
    }
}
