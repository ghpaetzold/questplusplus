/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * percentage of content words in the target
 *
 * @author cat
 *
 */
public class Feature1084 extends Feature {

    public Feature1084() {
        setIndex(1084);
        setDescription("percentage of content words in the target");
        this.addResource("target.postagger");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float noWords = target.getNoTokens();
        float noContent = (Integer) target.getValue("contentWords");
        setValue(noContent / noWords);

    }
}
