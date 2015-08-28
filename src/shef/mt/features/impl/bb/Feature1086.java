package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * LM log probability of POS of the target
 *
 * @author Catalina Hallett
 *
 */
public class Feature1086 extends Feature {

    public Feature1086() {
        setIndex(1086);
        setDescription("LM log probability of POS of the target");
        this.addResource("target.postagger");
        this.addResource("target.poslm");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        setValue((Float) target.getValue("poslogprob"));
    }
}
