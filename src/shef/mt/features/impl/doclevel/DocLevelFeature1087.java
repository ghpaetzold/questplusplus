package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;

import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * LM log perplexity of POS of the target
 *
 * @author cat
 *
 *
 */
public class DocLevelFeature1087 extends DocLevelFeature {

    public DocLevelFeature1087() {
        setIndex(1087);
        setDescription("LM log perplexity of POS of the target");
        this.addResource("posppl");
        
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
            total+=(float) target.getSentence(i).getValue("posppl");
        }
        setValue((float) total/target.getSentences().size());
    }
}
