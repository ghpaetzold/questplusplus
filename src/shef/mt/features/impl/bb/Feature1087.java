package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * LM log perplexity of POS of the target
 *
 * @author cat
 *
 *
 */
public class Feature1087 extends Feature {

    public Feature1087() {
        setIndex(1087);
        setDescription("LM log perplexity of POS of the target");
        this.addResource("target.postagger");
        this.addResource("target.poslm");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        setValue((Float) target.getValue("posppl"));
    }
}
