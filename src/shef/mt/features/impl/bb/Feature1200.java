/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * @author Catalina Hallett
 *
 */
public class Feature1200 extends Feature {

    public Feature1200() {
        setIndex("1200");
        setDescription("foo");
        this.addResource("source.postagger");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        int nouns = (Integer) source.getValue("nouns");
        setValue(nouns);
    }
}
