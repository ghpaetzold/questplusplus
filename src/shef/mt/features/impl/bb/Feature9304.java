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
public class Feature9304 extends Feature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public Feature9304() {
        setIndex(9304);
        setDescription("Target PCFG Parse log-likelihood");
       // this.addResource("BParser");
        this.addResource("target.bparser.grammar");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((Double) target.getValue("bparser.loglikelihood")));
    }

}
