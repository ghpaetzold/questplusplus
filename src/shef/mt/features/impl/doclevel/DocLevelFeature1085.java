/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Ratio of percentage of content words in the source and target documents
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1085 extends DocLevelFeature {

    public DocLevelFeature1085() {
        this.setIndex(1085);
        this.setDescription("ratio of percentage of content words in the source and target documents");
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
        float noWords=0;
        float noContent=0;
        for (int i=0;i<source.getSentences().size();i++){
            noWords += source.getSentence(i).getNoTokens();
            noContent += (Integer) source.getSentence(i).getValue("contentWords");
        }
        float perc1 = noContent / noWords;
        noWords=0;
        noContent=0;
        for (int i=0;i<target.getSentences().size();i++){
            noWords += target.getSentence(i).getNoTokens();
            noContent += (Integer) target.getSentence(i).getValue("contentWords");
        }
        float perc2 = noContent / noWords;
        if (perc1 == 0 || perc2 == 0) {
            setValue(0);
        } else {
            setValue(perc1 / perc2);
        }
    }
}
