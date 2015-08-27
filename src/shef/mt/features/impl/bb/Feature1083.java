/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import java.util.*;
import shef.mt.features.util.Sentence;

/**
 *
 * percentage of content words in the source
 *
 * @author cat
 *
 */
public class Feature1083 extends Feature {

    public Feature1083() {
        setIndex(1083);
        setDescription("percentage of content words in the source");
        this.addResource("source.postagger");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float noWords = source.getNoTokens();
        float noContent = (Integer) source.getValue("contentWords");
        setValue(noContent / noWords);
    }
}
