/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * Percentage of nouns in the source document
 *
 * @author Carolina Scarton
 *
 */
public class Feature1088 extends Feature {

    public Feature1088() {
        this.setIndex(1088);
        this.setDescription("percentage of nouns in the source document");
        this.addResource("source.postagger");
    }
    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        int noWords = source.getNoTokens();
        float noNouns = (Integer) source.getValue("nouns");
        setValue(noNouns / noWords);

    }
}
