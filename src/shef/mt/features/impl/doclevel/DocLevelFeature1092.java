/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 *
 * Ratio of percentage of nouns in the source and target documents
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1092 extends DocLevelFeature {

    public DocLevelFeature1092() {
        this.setIndex(1092);
        this.setDescription("ratio of percentage of nouns in the source and target documents");
        this.addResource("source.postagger");
        this.addResource("target.postagger");
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
        float noWords = 0;
        float noContent = 0;
        for (int i=0;i<source.getSentences().size();i++){
            noContent+=(Integer) source.getSentence(i).getValue("nouns");
            noWords+=source.getSentence(i).getNoTokens();
        }
        
        float perc1 = (float) noContent / noWords;
        noContent=0;
        noWords=0;
        for (int i=0;i<target.getSentences().size();i++){
            noWords = target.getSentence(i).getNoTokens();
            noContent = (Integer) target.getSentence(i).getValue("nouns");
        }
        float perc2 = (float) noContent / noWords;
        if (perc2 == 0) {
            setValue(0);
        } else {
            setValue(perc1 / perc2);
        }
    }
}
