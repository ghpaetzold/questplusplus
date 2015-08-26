/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * PCFG Parse log likelihood
 *
 * @author Eleftherios Avramidis
 */
public class Feature9307 extends Feature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public Feature9307() {
        setIndex(9303);
        setDescription("Count of possible target PCFG parses (n)");
       // this.addResource("BParser");
         this.addResource("target.bparser.grammar");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((Integer) target.getValue("bparser.n")));
    }

}
