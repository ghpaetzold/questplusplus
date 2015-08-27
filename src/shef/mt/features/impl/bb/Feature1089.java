/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * percentage of verbs in the source
 *
 * @author cat
 *
 */
public class Feature1089 extends Feature {

    public Feature1089() {
        setIndex(1089);
        setDescription("percentage of verbs in the source");
        this.addResource("source.postagger");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float noWords = source.getNoTokens();
        float noVerbs = (Integer) source.getValue("verbs");
        setValue(noVerbs / noWords);

    }
}
