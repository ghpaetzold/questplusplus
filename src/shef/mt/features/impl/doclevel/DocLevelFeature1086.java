package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * LM log probability of POS of the target document
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1086 extends DocLevelFeature {

    public DocLevelFeature1086() {
        this.setIndex(1086);
        this.setDescription("LM log probability of POS of the target document");
        this.addResource("target.poslm");
        
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
        float total = 0.0f;
        for (int i=0;i<target.getSentences().size();i++){
            total+=(float) target.getSentence(i).getValue("poslogprob");
        }
        setValue((float) total/target.getSentences().size());
    }
}
